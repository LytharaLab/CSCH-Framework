package org.lytharalab.csch.opencl;

import org.jocl.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class OpenCLContext {
    private static final Logger logger = LoggerFactory.getLogger(OpenCLContext.class);
    
    private cl_context context;
    private cl_command_queue commandQueue;
    private cl_device_id device;
    private cl_platform_id platform;
    
    private boolean initialized = false;
    private String deviceName;
    private String platformName;
    private long maxWorkGroupSize;
    private long globalMemSize;
    private long localMemSize;
    
    public void initialize() {
        if (initialized) {
            return;
        }
        
        try {
            int[] numPlatformsArray = new int[1];
            CL.clGetPlatformIDs(0, null, numPlatformsArray);
            int numPlatforms = numPlatformsArray[0];
            
            cl_platform_id[] platforms = new cl_platform_id[numPlatforms];
            CL.clGetPlatformIDs(numPlatforms, platforms, null);
            
            platform = platforms[0];
            
            long[] size = new long[1];
            byte[] buffer = new byte[256];
            CL.clGetPlatformInfo(platform, CL.CL_PLATFORM_NAME, 256, Pointer.to(buffer), size);
            platformName = new String(buffer, 0, (int) size[0] - 1);
            
            int[] numDevicesArray = new int[1];
            CL.clGetDeviceIDs(platform, CL.CL_DEVICE_TYPE_GPU, 0, null, numDevicesArray);
            int numDevices = numDevicesArray[0];
            
            if (numDevices == 0) {
                CL.clGetDeviceIDs(platform, CL.CL_DEVICE_TYPE_CPU, 0, null, numDevicesArray);
                numDevices = numDevicesArray[0];
                
                cl_device_id[] devices = new cl_device_id[numDevices];
                CL.clGetDeviceIDs(platform, CL.CL_DEVICE_TYPE_CPU, numDevices, devices, null);
                device = devices[0];
            } else {
                cl_device_id[] devices = new cl_device_id[numDevices];
                CL.clGetDeviceIDs(platform, CL.CL_DEVICE_TYPE_GPU, numDevices, devices, null);
                device = devices[0];
            }
            
            buffer = new byte[256];
            CL.clGetDeviceInfo(device, CL.CL_DEVICE_NAME, 256, Pointer.to(buffer), size);
            deviceName = new String(buffer, 0, (int) size[0] - 1);
            
            long[] maxWorkGroupSizeArray = new long[1];
            CL.clGetDeviceInfo(device, CL.CL_DEVICE_MAX_WORK_GROUP_SIZE, 
                Sizeof.size_t, Pointer.to(maxWorkGroupSizeArray), null);
            maxWorkGroupSize = maxWorkGroupSizeArray[0];
            
            long[] globalMemSizeArray = new long[1];
            CL.clGetDeviceInfo(device, CL.CL_DEVICE_GLOBAL_MEM_SIZE,
                Sizeof.cl_long, Pointer.to(globalMemSizeArray), null);
            globalMemSize = globalMemSizeArray[0];
            
            long[] localMemSizeArray = new long[1];
            CL.clGetDeviceInfo(device, CL.CL_DEVICE_LOCAL_MEM_SIZE,
                Sizeof.cl_long, Pointer.to(localMemSizeArray), null);
            localMemSize = localMemSizeArray[0];
            
            int[] errcode_ret = new int[1];
            context = CL.clCreateContext(null, 1, new cl_device_id[]{device}, null, null, errcode_ret);
            
            commandQueue = CL.clCreateCommandQueue(context, device, 0, errcode_ret);
            
            initialized = true;
            
            logger.info("OpenCL context initialized");
            logger.info("Platform: {}", platformName);
            logger.info("Device: {}", deviceName);
            logger.info("Max Work Group Size: {}", maxWorkGroupSize);
            logger.info("Global Memory: {} MB", globalMemSize / (1024 * 1024));
            logger.info("Local Memory: {} KB", localMemSize / 1024);
            
        } catch (Exception e) {
            logger.error("Failed to initialize OpenCL context", e);
            cleanup();
            throw new RuntimeException("Failed to initialize OpenCL context", e);
        }
    }
    
    public void cleanup() {
        if (commandQueue != null) {
            CL.clReleaseCommandQueue(commandQueue);
            commandQueue = null;
        }
        if (context != null) {
            CL.clReleaseContext(context);
            context = null;
        }
        initialized = false;
        logger.info("OpenCL context cleaned up");
    }
    
    public cl_context getContext() {
        return context;
    }
    
    public cl_command_queue getCommandQueue() {
        return commandQueue;
    }
    
    public cl_device_id getDevice() {
        return device;
    }
    
    public cl_platform_id getPlatform() {
        return platform;
    }
    
    public boolean isInitialized() {
        return initialized;
    }
    
    public String getDeviceName() {
        return deviceName;
    }
    
    public String getPlatformName() {
        return platformName;
    }
    
    public long getMaxWorkGroupSize() {
        return maxWorkGroupSize;
    }
    
    public long getGlobalMemSize() {
        return globalMemSize;
    }
    
    public long getLocalMemSize() {
        return localMemSize;
    }
    
    public void finish() {
        if (commandQueue != null) {
            CL.clFinish(commandQueue);
        }
    }
}
