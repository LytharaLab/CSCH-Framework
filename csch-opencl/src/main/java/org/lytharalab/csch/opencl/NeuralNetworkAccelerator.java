package org.lytharalab.csch.opencl;

import org.jocl.Sizeof;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NeuralNetworkAccelerator {
    private static final Logger logger = LoggerFactory.getLogger(NeuralNetworkAccelerator.class);
    
    private final OpenCLManager openCLManager;
    private boolean useGPU;
    
    public NeuralNetworkAccelerator(boolean useGPU) {
        this.useGPU = useGPU;
        this.openCLManager = OpenCLManager.getInstance();
        
        if (useGPU) {
            try {
                openCLManager.initialize();
                logger.info("NeuralNetworkAccelerator initialized with GPU acceleration");
            } catch (Exception e) {
                logger.warn("Failed to initialize GPU acceleration, falling back to CPU", e);
                this.useGPU = false;
            }
        }
    }
    
    public NeuralNetworkAccelerator() {
        this(false);
    }
    
    public float[] forwardPass(float[] input, float[][] weights, float[] bias) {
        if (!useGPU) {
            return forwardPassCPU(input, weights, bias);
        }
        
        return forwardPassGPU(input, weights, bias);
    }
    
    private float[] forwardPassCPU(float[] input, float[][] weights, float[] bias) {
        int outputSize = weights.length;
        int inputSize = input.length;
        
        float[] output = new float[outputSize];
        
        for (int i = 0; i < outputSize; i++) {
            float sum = bias[i];
            for (int j = 0; j < inputSize; j++) {
                sum += input[j] * weights[i][j];
            }
            output[i] = (float) (1.0 / (1.0 + Math.exp(-sum)));
        }
        
        return output;
    }
    
    private float[] forwardPassGPU(float[] input, float[][] weights, float[] bias) {
        int outputSize = weights.length;
        int inputSize = input.length;
        
        float[] flatWeights = flattenWeights(weights);
        
        OpenCLBuffer inputBuffer = OpenCLBuffer.createReadOnly(openCLManager.getContext(), inputSize * 4L);
        OpenCLBuffer weightsBuffer = OpenCLBuffer.createReadOnly(openCLManager.getContext(), flatWeights.length * 4L);
        OpenCLBuffer biasBuffer = OpenCLBuffer.createReadOnly(openCLManager.getContext(), outputSize * 4L);
        OpenCLBuffer outputBuffer = OpenCLBuffer.createWriteOnly(openCLManager.getContext(), outputSize * 4L);
        
        try {
            inputBuffer.write(input);
            weightsBuffer.write(flatWeights);
            biasBuffer.write(bias);
            
            OpenCLProgram program = openCLManager.getProgram();
            program.setKernelArg("matrix_mul", 0, inputBuffer.getBuffer());
            program.setKernelArg("matrix_mul", 1, weightsBuffer.getBuffer());
            program.setKernelArg("matrix_mul", 2, outputBuffer.getBuffer());
            program.setKernelArg("matrix_mul", 3, outputSize);
            program.setKernelArg("matrix_mul", 4, inputSize);
            program.setKernelArg("matrix_mul", 5, 1);
            
            long[] globalWorkSize = {(long) outputSize, 1};
            program.executeKernel("matrix_mul", globalWorkSize, null);
            
            float[] output = outputBuffer.readFloat(outputSize);
            
            return openCLManager.applySigmoid(output);
        } finally {
            inputBuffer.cleanup();
            weightsBuffer.cleanup();
            biasBuffer.cleanup();
            outputBuffer.cleanup();
        }
    }
    
    private float[] flattenWeights(float[][] weights) {
        int rows = weights.length;
        int cols = weights[0].length;
        float[] flat = new float[rows * cols];
        
        for (int i = 0; i < rows; i++) {
            System.arraycopy(weights[i], 0, flat, i * cols, cols);
        }
        
        return flat;
    }
    
    public float[] pidControl(float[] error, float[] integral, float[] derivative,
                             float kp, float ki, float kd) {
        if (!useGPU) {
            return pidControlCPU(error, integral, derivative, kp, ki, kd);
        }
        
        return pidControlGPU(error, integral, derivative, kp, ki, kd);
    }
    
    private float[] pidControlCPU(float[] error, float[] integral, float[] derivative,
                                  float kp, float ki, float kd) {
        int n = error.length;
        float[] output = new float[n];
        
        for (int i = 0; i < n; i++) {
            output[i] = kp * error[i] + ki * integral[i] + kd * derivative[i];
        }
        
        return output;
    }
    
    private float[] pidControlGPU(float[] error, float[] integral, float[] derivative,
                                  float kp, float ki, float kd) {
        int n = error.length;
        
        OpenCLBuffer errorBuffer = OpenCLBuffer.createReadOnly(openCLManager.getContext(), n * 4L);
        OpenCLBuffer integralBuffer = OpenCLBuffer.createReadOnly(openCLManager.getContext(), n * 4L);
        OpenCLBuffer derivativeBuffer = OpenCLBuffer.createReadOnly(openCLManager.getContext(), n * 4L);
        OpenCLBuffer outputBuffer = OpenCLBuffer.createWriteOnly(openCLManager.getContext(), n * 4L);
        
        try {
            errorBuffer.write(error);
            integralBuffer.write(integral);
            derivativeBuffer.write(derivative);
            
            OpenCLProgram program = openCLManager.getProgram();
            program.setKernelArg("pid_control", 0, errorBuffer.getBuffer());
            program.setKernelArg("pid_control", 1, integralBuffer.getBuffer());
            program.setKernelArg("pid_control", 2, derivativeBuffer.getBuffer());
            program.setKernelArg("pid_control", 3, outputBuffer.getBuffer());
            program.setKernelArg("pid_control", 4, kp);
            program.setKernelArg("pid_control", 5, ki);
            program.setKernelArg("pid_control", 6, kd);
            program.setKernelArg("pid_control", 7, n);
            
            long[] globalWorkSize = {n};
            program.executeKernel("pid_control", globalWorkSize, null);
            
            return outputBuffer.readFloat(n);
        } finally {
            errorBuffer.cleanup();
            integralBuffer.cleanup();
            derivativeBuffer.cleanup();
            outputBuffer.cleanup();
        }
    }
    
    public boolean isGPUEnabled() {
        return useGPU;
    }
    
    public void shutdown() {
        if (useGPU) {
            openCLManager.shutdown();
        }
    }
}
