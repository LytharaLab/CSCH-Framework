package org.lytharalab.csch.opencl;

import org.jocl.CL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpenCLManager {
    private static final Logger logger = LoggerFactory.getLogger(OpenCLManager.class);
    
    private static OpenCLManager instance;
    private final OpenCLContext context;
    private final OpenCLProgram program;
    private boolean initialized = false;
    
    private OpenCLManager() {
        this.context = new OpenCLContext();
        this.program = new OpenCLProgram(context);
    }
    
    public static synchronized OpenCLManager getInstance() {
        if (instance == null) {
            instance = new OpenCLManager();
        }
        return instance;
    }
    
    public synchronized void initialize() {
        if (initialized) {
            return;
        }
        
        try {
            context.initialize();
            loadDefaultKernels();
            initialized = true;
            logger.info("OpenCLManager initialized successfully");
        } catch (Exception e) {
            logger.error("Failed to initialize OpenCLManager", e);
            throw new RuntimeException("Failed to initialize OpenCLManager", e);
        }
    }
    
    private void loadDefaultKernels() {
        String kernelSource = buildDefaultKernelSource();
        program.loadFromSource(kernelSource);
        
        program.createKernel("vector_add");
        program.createKernel("vector_mul");
        program.createKernel("matrix_mul");
        program.createKernel("sigmoid");
        program.createKernel("relu");
        program.createKernel("softmax");
        program.createKernel("pid_control");
        
        logger.info("Default kernels loaded");
    }
    
    private String buildDefaultKernelSource() {
        return """
            __kernel void vector_add(
                __global const float* a,
                __global const float* b,
                __global float* c,
                const unsigned int n
            ) {
                int id = get_global_id(0);
                if (id < n) {
                    c[id] = a[id] + b[id];
                }
            }
            
            __kernel void vector_mul(
                __global const float* a,
                __global const float* b,
                __global float* c,
                const unsigned int n
            ) {
                int id = get_global_id(0);
                if (id < n) {
                    c[id] = a[id] * b[id];
                }
            }
            
            __kernel void matrix_mul(
                __global const float* a,
                __global const float* b,
                __global float* c,
                const unsigned int m,
                const unsigned int n,
                const unsigned int k
            ) {
                int row = get_global_id(0);
                int col = get_global_id(1);
                
                if (row < m && col < k) {
                    float sum = 0.0f;
                    for (int i = 0; i < n; i++) {
                        sum += a[row * n + i] * b[i * k + col];
                    }
                    c[row * k + col] = sum;
                }
            }
            
            __kernel void sigmoid(
                __global const float* input,
                __global float* output,
                const unsigned int n
            ) {
                int id = get_global_id(0);
                if (id < n) {
                    output[id] = 1.0f / (1.0f + exp(-input[id]));
                }
            }
            
            __kernel void relu(
                __global const float* input,
                __global float* output,
                const unsigned int n
            ) {
                int id = get_global_id(0);
                if (id < n) {
                    output[id] = fmax(0.0f, input[id]);
                }
            }
            
            __kernel void softmax(
                __global const float* input,
                __global float* output,
                const unsigned int n
            ) {
                int id = get_global_id(0);
                if (id < n) {
                    float max_val = input[0];
                    for (int i = 1; i < n; i++) {
                        if (input[i] > max_val) max_val = input[i];
                    }
                    
                    float sum = 0.0f;
                    for (int i = 0; i < n; i++) {
                        sum += exp(input[i] - max_val);
                    }
                    
                    output[id] = exp(input[id] - max_val) / sum;
                }
            }
            
            __kernel void pid_control(
                __global const float* error,
                __global const float* integral,
                __global const float* derivative,
                __global float* output,
                const float kp,
                const float ki,
                const float kd,
                const unsigned int n
            ) {
                int id = get_global_id(0);
                if (id < n) {
                    output[id] = kp * error[id] + ki * integral[id] + kd * derivative[id];
                }
            }
            """;
    }
    
    public OpenCLContext getContext() {
        return context;
    }
    
    public OpenCLProgram getProgram() {
        return program;
    }
    
    public boolean isInitialized() {
        return initialized;
    }
    
    public synchronized void shutdown() {
        if (!initialized) {
            return;
        }
        
        program.cleanup();
        context.cleanup();
        initialized = false;
        logger.info("OpenCLManager shutdown complete");
    }
    
    public void finish() {
        context.finish();
    }
    
    public float[] vectorAdd(float[] a, float[] b) {
        int n = a.length;
        
        OpenCLBuffer bufferA = OpenCLBuffer.createReadOnly(context, n * 4L);
        OpenCLBuffer bufferB = OpenCLBuffer.createReadOnly(context, n * 4L);
        OpenCLBuffer bufferC = OpenCLBuffer.createWriteOnly(context, n * 4L);
        
        try {
            bufferA.write(a);
            bufferB.write(b);
            
            program.setKernelArg("vector_add", 0, bufferA.getBuffer());
            program.setKernelArg("vector_add", 1, bufferB.getBuffer());
            program.setKernelArg("vector_add", 2, bufferC.getBuffer());
            program.setKernelArg("vector_add", 3, n);
            
            long[] globalWorkSize = {n};
            program.executeKernel("vector_add", globalWorkSize, null);
            
            return bufferC.readFloat(n);
        } finally {
            bufferA.cleanup();
            bufferB.cleanup();
            bufferC.cleanup();
        }
    }
    
    public float[] applySigmoid(float[] input) {
        int n = input.length;
        
        OpenCLBuffer bufferIn = OpenCLBuffer.createReadOnly(context, n * 4L);
        OpenCLBuffer bufferOut = OpenCLBuffer.createWriteOnly(context, n * 4L);
        
        try {
            bufferIn.write(input);
            
            program.setKernelArg("sigmoid", 0, bufferIn.getBuffer());
            program.setKernelArg("sigmoid", 1, bufferOut.getBuffer());
            program.setKernelArg("sigmoid", 2, n);
            
            long[] globalWorkSize = {n};
            program.executeKernel("sigmoid", globalWorkSize, null);
            
            return bufferOut.readFloat(n);
        } finally {
            bufferIn.cleanup();
            bufferOut.cleanup();
        }
    }
}
