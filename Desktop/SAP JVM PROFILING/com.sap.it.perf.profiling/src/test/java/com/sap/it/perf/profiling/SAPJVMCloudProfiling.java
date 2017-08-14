package com.sap.it.perf.profiling;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;

import com.sap.jvm.debugging.controller.io.CloudDebuggingPoller;
import com.sap.jvm.debugging.impl.cloudhelper.CloudProfilingServiceImpl;
import com.sap.jvm.profiling.controller.ControllerFactory;
import com.sap.jvm.profiling.controller.RemoteController;
import com.sap.jvm.profiling.controller.action.ActionFactory;
import com.sap.jvm.profiling.controller.action.ActionResponse;
import com.sap.jvm.profiling.controller.action.ActionResponseSet;
import com.sap.jvm.profiling.controller.action.ActionSet;
import com.sap.jvm.profiling.controller.model.ModelController;
import com.sap.jvm.profiling.controller.trace.AllocationTraceSpec;
import com.sap.jvm.profiling.controller.trace.GcTraceSpec;
import com.sap.jvm.profiling.controller.trace.IOTraceSpec;
import com.sap.jvm.profiling.controller.trace.NetworkTraceSpec;
import com.sap.jvm.profiling.controller.trace.PerformanceHotspotTraceSpec;
import com.sap.jvm.profiling.controller.trace.ProfilingTraceSpec;
import com.sap.jvm.profiling.controller.trace.SynchronizationTraceSpec;
import com.sap.jvm.profiling.controller.trace.ThreadDumpTraceSpec;
import com.sap.jvm.profiling.core.ProfilingTraceType;
import com.sap.jvm.profiling.core.command.EnableHeartBeatCommand;
import com.sap.jvm.profiling.thread.command.ThreadDumpConfiguration;
import com.sap.jvm.util.http.CloudService;
import com.sap.jvm.util.http.HttpPoller;
import com.sap.jvm.util.http.PrefixedInputStream;
import com.sap.jvm.util.misc.SocketAdapter;

public class SAPJVMCloudProfiling {
   private static Logger log = Logger.getLogger(SAPJVMCloudProfiling.class.getName());

   private static final byte[] profilingServerVersion = { 0x0, 0x1, 0x0, 0x0 };

   private static AtomicLong idPart = new AtomicLong();

   private static final String SELECT_ALL = "all";
   private static final String SELECT_TOP = "top 20";
   
   private static final int TEMP_DIR_ATTEMPTS = 10000;
   
   // PerformanceHotspotTraceSpec
   // If isSmooth is enabled, the Java threads can be sampled at more places in the Java methods.
   // This leads to better results, but causes more runtime overhead.
   boolean isSmooth = false;
   boolean areSleepingThreadsIgnored = false;

   // AllocationTraceSpec
   boolean includeIds = false;
   boolean isAdaptive = true;
   boolean includeLineNrs = true;

   // NetworkTraceSpec
   boolean areAllHostsResolved;

   // ThreadDumpTraceSpec
   int numberThreadDumps = 2;
   int threadDumpsPeriod = 100;
   int threadDumpsDelay = 1000;

   String proxyHost = "proxy.wdf.sap.corp";
   int proxyPort = 8080;

   String serviceUrl;
   String account;
   String app;
   String user;
   String pwd;
   
   static {
      log.setLevel(Level.ALL);
   }

   public static synchronized String generateUniqueIdentifier() {
      // Make the id so it can be sorted in lexicographic order and be sorted by time.
      String prefix = "0000000000000000" + Long.toString(System.currentTimeMillis(), 16);
      return prefix.substring(prefix.length() - 16) + "-" + Long.toString(idPart.addAndGet(1), 16);
   }

   public static String removeLastWord(String str) {
      return str.substring(0, str.lastIndexOf(" "));
   }

   public static String getLastWord(String str) {
      return str.substring(str.lastIndexOf(".") + 1);
   }

   public static String capitalizeString(String string) {
      char[] chars = string.toLowerCase().toCharArray();
      boolean found = false;
      for (int i = 0; i < chars.length; i++) {
         if (Character.isLetter(chars[i])) {
            if (!found) {
               chars[i] = Character.toUpperCase(chars[i]);
            }
            found = true;
         } else {
            found = false;
         }
      }
      return String.valueOf(chars);
   }
   
   public void setProxyHost(String proxyHost) {
      this.proxyHost = proxyHost;
   }

   public String getProxyHost() {
      return proxyHost;
   }

   public void setProxyPort(int proxyPort) {
      this.proxyPort = proxyPort;
   }

   public int getProxyPort() {
      return proxyPort;
   }

   // from com.sap.jvm.profiling.ui.workbench.launching.ProfilingLaunchUtils
   public PerformanceHotspotTraceSpec createPerfTraceSpec() {
      PerformanceHotspotTraceSpec spec = new PerformanceHotspotTraceSpec();
      spec.setSmooth(isSmooth);
      if (areSleepingThreadsIgnored) {
         spec.ignoreSleepingThreads();
      } else {
         spec.reportSleepingThreads();
      }
      spec.setUserFilter(ProfilingTraceSpec.NO_FILTER);
      spec.setRequestedMethodParameters(null);
      return spec;
   }

   public AllocationTraceSpec createAllocationTraceSpec() {
      AllocationTraceSpec spec = new AllocationTraceSpec();
      isAdaptive = isAdaptive && !includeIds;
      spec.setIncludeIds(includeIds);
      spec.setIsAdaptive(isAdaptive);
      spec.setIncludeLineNrs(includeLineNrs);
      spec.setRequestedMethodParameters(null);
      return spec;
   }

   public IOTraceSpec createIOTraceSpec() {
      IOTraceSpec spec = new IOTraceSpec();
      return spec;
   }

   public GcTraceSpec createGcTraceSpec() {
      GcTraceSpec spec = new GcTraceSpec();
      return spec;
   }

   public SynchronizationTraceSpec createSynchronizationTraceSpec() {
      SynchronizationTraceSpec spec = new SynchronizationTraceSpec();
      return spec;
   }

   public NetworkTraceSpec createNetworkTraceSpec() {
      NetworkTraceSpec spec = new NetworkTraceSpec();
      spec.setResolveAllHosts(areAllHostsResolved);
      return spec;
   }

   public ThreadDumpTraceSpec createThreadDumpTraceSpec() {
      ThreadDumpConfiguration config = new ThreadDumpConfiguration(numberThreadDumps, threadDumpsPeriod,
            threadDumpsDelay);
      ThreadDumpTraceSpec spec = new ThreadDumpTraceSpec(config);
      return spec;
   }

   public SAPJVMCloudProfiling(String serviceUrl, String account, String app, String user, String pwd) {
      this.serviceUrl = serviceUrl;
      this.account = account;
      this.app = app;
      this.user = user;
      this.pwd = pwd;
   }

   public static File createTempDir() {
	   File baseDir = new File(System.getProperty("java.io.tmpdir"));
	   String baseName = System.currentTimeMillis() + "-";

	   for (int counter = 0; counter < TEMP_DIR_ATTEMPTS; counter++) {
	     File tempDir = new File(baseDir, baseName + counter);
	     if (tempDir.mkdir()) {
	       return tempDir;
	     }
	   }
	   throw new IllegalStateException("Failed to create directory within "
	       + TEMP_DIR_ATTEMPTS + " attempts (tried "
	       + baseName + "0 to " + baseName + (TEMP_DIR_ATTEMPTS - 1) + ')');
	 }
   
   private void createDir(File prfFile) throws IOException {
	  String parent = prfFile.getParent();
	  if (parent != null) {
	      (new File(parent)).mkdirs();
	  }
   }
   
   private void delete(File file) {
      File[] contents = file.listFiles();
      if (contents != null) {
          for (File f : contents) {
              delete(f);
          }
      }
      file.delete();
  }
   
   // Extracted from
   // com.sap.jvm.profiling.ui.launching.configuration.OnlineProfilingLaunchConfigurationDelegate
   // com.sap.jvm.profiling.ui.launching.configuration.CloudProfilingDelegate
   public void profile(File prfFile, ProfilingTraceSpec spec, long profileDuration, long ignoreDuration)
         throws IOException, URISyntaxException, InterruptedException {
      Map<String, String> params = new HashMap<String, String>();
      params.put("account", account);
      params.put("app", app);
      params.put("component", "web");
      params.put("profiling", "true");
      Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));

      String profilerName = getLastWord(spec.getClass().getName());
      if (prfFile == null) {
         prfFile = new File(System.getProperty("user.dir") + File.separator + profilerName + File.separator + profilerName + ".prf");
      }
      createDir(prfFile);
      File tmpDir = createTempDir();

      RemoteController controller = null;
      SocketAdapter adapter = null;
      try {
         final CloudService cloudService = new CloudProfilingServiceImpl(serviceUrl, user, pwd, params, proxy);
         cloudService.start();
         cloudService.connect();
         log.fine("Cloud profiling service connected to account " + account + ", app " + app + " with user " + user
               + " to landscape " + serviceUrl);

         final HttpPoller poller = new CloudDebuggingPoller((CloudService) cloudService, null); 
         adapter = new SocketAdapter() {
            /**
             * The filtered input stream prepending a profiling server version to the stream.
             */
            private final InputStream is = new PrefixedInputStream(poller.getInputStream(), profilingServerVersion);

            /**
             * @see com.sap.jvm.util.misc.SocketAdapter#getOutputStream()
             */
            public OutputStream getOutputStream() throws IOException {
               return poller.getOutputStream();
            }

            /**
             * @see com.sap.jvm.util.misc.SocketAdapter#getInputStream()
             */
            public InputStream getInputStream() throws IOException {
               return is;
            }

            /**
             * @see com.sap.jvm.util.misc.SocketAdapter#close()
             */
            public void close() throws IOException {
               getOutputStream().close();
               getInputStream().close();
               try {
                  cloudService.disconnect();
                  cloudService.stop();
               } catch (final Exception e) {
                  log.log(Level.SEVERE, e.getMessage());
               }
            }
         };

         controller = ControllerFactory.createRemoteController(prfFile.getAbsolutePath(),
               tmpDir.getCanonicalPath(), tmpDir, false);
         controller.setId(generateUniqueIdentifier());
         controller.setInitHeartBeatInterval(EnableHeartBeatCommand.DEFAULT_HEART_BEAT_INTERVAL);
         controller.connectSocket(adapter);
         log.fine("Profiling controller connected.");

         ActionFactory actionFactory = controller.getActionFactory();
         ActionSet actions = new ActionSet();
         actions.add(actionFactory.getEnableAction(spec.getTraceName(), spec));
         actions.add(actionFactory.getFlushAction());
         ActionResponseSet responses = controller.applyActions(actions);
         log.fine("Applied action " + profilerName + " to profiling controller.");
         boolean isError = false;
         for (int i = 0; i < responses.size(); i++) {
            ActionResponse response = responses.get(i);
            if (!response.success()) {
               log.severe(response.getErrorMessage());
               isError = true;
            }
         }
         if (isError) {
            return;
         }

         log.fine("Ignore data for next " + ignoreDuration + "ms ...");
         Thread.sleep(ignoreDuration);
         ModelController modelController = controller.getModelController();
         modelController.createSnapshots();
         log.fine("Snapshot created");
         
         if (!(spec instanceof GcTraceSpec || spec instanceof ThreadDumpTraceSpec)) {
            log.fine("Collecting data for next " + profileDuration + "ms ...");
            Thread.sleep(profileDuration);
/*            modelController.createSnapshot(ProfilingTraceType.ALL_TRACES, true);
            log.fine("Snapshot created");*/
         }
      } finally {
         if (controller != null) {
            controller.close(true);
         }
         if (adapter != null) {
            adapter.close();
         }
         log.fine("Profiling connection closed.");
         
         delete(tmpDir);
      }
   }

   public void profile(File prfFile, ProfilingTraceSpec spec) throws IOException, URISyntaxException, InterruptedException {
      profile(prfFile, spec, 0, 0);
   }
   
   public static void main(String[] args) throws Exception {
	  
   }
}