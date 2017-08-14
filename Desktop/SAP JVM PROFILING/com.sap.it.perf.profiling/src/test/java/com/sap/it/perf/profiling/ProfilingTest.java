package com.sap.it.perf.profiling;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.Test;

public class ProfilingTest {

	@Test
	public void Profilestart(){	
		String duration = System.getProperty("duration");
		Long duration1 = new Long(duration).longValue();
		String analysis = System.getProperty("analysis");
		SAPJVMCloudProfiling p= new SAPJVMCloudProfiling(System.getProperty("url"),System.getProperty("Acc"),System.getProperty("app"),System.getProperty("user"),System.getProperty("pass"));
		if (analysis.equals("responsetime")) 
	{
		try {
			p.profile(null, p.createPerfTraceSpec(), duration1, 2000);
			File file = new File(System.getProperty("basedir")+"/PerformanceHotspotTraceSpec/PerformanceHotspotTraceSpec.prf");
			assertTrue(file.exists());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	else if(analysis.equals("allocation")) {
		try {
			p.profile(null,p.createAllocationTraceSpec(),duration1,2000);
			File file = new File(System.getProperty("basedir")+"/AllocationTraceSpec/AllocationTraceSpec.prf");
			assertTrue(file.exists());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	} 
	else if(analysis.equals("io")) {
		try {
			p.profile(null, p.createIOTraceSpec(), duration1, 2000);
			File file = new File(System.getProperty("basedir")+"/IOTraceSpec/IOTraceSpec.prf");
			assertTrue(file.exists());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	} 
	else if(analysis.equals("gc")) {
		try {
			p.profile(null, p.createGcTraceSpec());
			File file = new File(System.getProperty("basedir")+"/GcTraceSpec/GcTraceSpec.prf");
			assertTrue(file.exists());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	} 
	else if(analysis.equals("sync")) {
		try {
			p.profile(null, p.createSynchronizationTraceSpec(), duration1, 2000);
			File file = new File(System.getProperty("basedir")+"/SynchronizationTraceSpec/SynchronizationTraceSpec.prf");
			assertTrue(file.exists());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	else if(analysis.equals("network")) {
		try {
			p.profile(null, p.createNetworkTraceSpec(), duration1, 2000);
			File file = new File(System.getProperty("basedir")+"/NetworkTraceSpec/NetworkTraceSpec.prf");
			assertTrue(file.exists());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	else if(analysis.equals("thread")) {
		try {
			p.profile(null, p.createThreadDumpTraceSpec());
			File file = new File(System.getProperty("basedir")+"/ThreadDumpTraceSpec/ThreadDumpTraceSpec.prf");
			assertTrue(file.exists());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	else {
		fail("Profiling type not valid");
	}
	
	}

	}