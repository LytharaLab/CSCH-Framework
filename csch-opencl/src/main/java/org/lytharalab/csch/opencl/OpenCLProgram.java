package org.lytharalab.csch.opencl;

import org.jocl.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class OpenCLProgram {
    private static final Logger logger = LoggerFactory.getLogger(OpenCLProgram.class);
    
    private final OpenCLContext context;
    private final Map<String, cl_kernel> kernels;
    private cl_program program;
    
    public OpenCLProgram(OpenCLContext context) {
        this.context = context;
        this.kernels = new HashMap<>();
    }
    
    public void loadFromSource(String source) {
        if (!context.isInitialized()) {
            throw new IllegalStateException("OpenCL context not initialized");
        }
        
        int[] errcode_ret = new int[1];
        
        program = CL.clCreateProgramWithSource(
            context.getContext(), 
            1, 
            new String[]{source}, 
            null, 
            errcode_ret
        );
        
        if (errcode_ret[0] != CL.CL_SUCCESS) {
            throw new RuntimeException("Failed to create program: " + errcode_ret[0]);
        }
        
        int buildResult = CL.clBuildProgram(
            program, 
            0, 
            null, 
            null, 
            null, 
            null
        );
        
        if (buildResult != CL.CL_SUCCESS) {
            long[] logSize = new long[1];
            CL.clGetProgramBuildInfo(
                program, 
                context.getDevice(), 
                CL.CL_PROGRAM_BUILD_LOG, 
                0, 
                null, 
                logSize
            );
            
            byte[] logData = new byte[(int) logSize[0]];
            CL.clGetProgramBuildInfo(
                program, 
                context.getDevice(), 
                CL.CL_PROGRAM_BUILD_LOG, 
                logSize[0], 
                Pointer.to(logData), 
                null
            );
            
            String buildLog = new String(logData, 0, (int) logSize[0] - 1);
            logger.error("Build error: {}", buildLog);
            throw new RuntimeException("Failed to build program: " + buildLog);
        }
        
        logger.info("OpenCL program built successfully");
    }
    
    public void loadFromFile(Path filePath) throws IOException {
        String source = Files.readString(filePath);
        loadFromSource(source);
    }
    
    public void createKernel(String kernelName) {
        if (program == null) {
            throw new IllegalStateException("Program not loaded");
        }
        
        int[] errcode_ret = new int[1];
        cl_kernel kernel = CL.clCreateKernel(program, kernelName, errcode_ret);
        
        if (errcode_ret[0] != CL.CL_SUCCESS) {
            throw new RuntimeException("Failed to create kernel '" + kernelName + "': " + errcode_ret[0]);
        }
        
        kernels.put(kernelName, kernel);
        logger.debug("Created kernel: {}", kernelName);
    }
    
    public cl_kernel getKernel(String kernelName) {
        return kernels.get(kernelName);
    }
    
    public void setKernelArg(String kernelName, int argIndex, cl_mem buffer) {
        cl_kernel kernel = kernels.get(kernelName);
        if (kernel == null) {
            throw new IllegalArgumentException("Kernel not found: " + kernelName);
        }
        
        CL.clSetKernelArg(kernel, argIndex, Sizeof.cl_mem, Pointer.to(buffer));
    }
    
    public void setKernelArg(String kernelName, int argIndex, float value) {
        cl_kernel kernel = kernels.get(kernelName);
        if (kernel == null) {
            throw new IllegalArgumentException("Kernel not found: " + kernelName);
        }
        
        CL.clSetKernelArg(kernel, argIndex, Sizeof.cl_float, Pointer.to(new float[]{value}));
    }
    
    public void setKernelArg(String kernelName, int argIndex, int value) {
        cl_kernel kernel = kernels.get(kernelName);
        if (kernel == null) {
            throw new IllegalArgumentException("Kernel not found: " + kernelName);
        }
        
        CL.clSetKernelArg(kernel, argIndex, Sizeof.cl_int, Pointer.to(new int[]{value}));
    }
    
    public void executeKernel(String kernelName, long[] globalWorkSize, long[] localWorkSize) {
        cl_kernel kernel = kernels.get(kernelName);
        if (kernel == null) {
            throw new IllegalArgumentException("Kernel not found: " + kernelName);
        }
        
        CL.clEnqueueNDRangeKernel(
            context.getCommandQueue(),
            kernel,
            globalWorkSize.length,
            null,
            globalWorkSize,
            localWorkSize,
            0,
            null,
            null
        );
    }
    
    public void cleanup() {
        for (cl_kernel kernel : kernels.values()) {
            CL.clReleaseKernel(kernel);
        }
        kernels.clear();
        
        if (program != null) {
            CL.clReleaseProgram(program);
            program = null;
        }
        
        logger.info("OpenCL program cleaned up");
    }
}
