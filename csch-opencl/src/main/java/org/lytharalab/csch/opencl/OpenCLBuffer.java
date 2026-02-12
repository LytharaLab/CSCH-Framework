package org.lytharalab.csch.opencl;

import org.jocl.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpenCLBuffer {
    private static final Logger logger = LoggerFactory.getLogger(OpenCLBuffer.class);
    
    private final OpenCLContext context;
    private cl_mem buffer;
    private final long size;
    private final long flags;
    
    public OpenCLBuffer(OpenCLContext context, long size, long flags) {
        this.context = context;
        this.size = size;
        this.flags = flags;
        
        if (!context.isInitialized()) {
            throw new IllegalStateException("OpenCL context not initialized");
        }
        
        int[] errcode_ret = new int[1];
        buffer = CL.clCreateBuffer(context.getContext(), flags, size, null, errcode_ret);
        
        if (errcode_ret[0] != CL.CL_SUCCESS) {
            throw new RuntimeException("Failed to create buffer: " + errcode_ret[0]);
        }
        
        logger.debug("Created OpenCL buffer of size {} bytes", size);
    }
    
    public static OpenCLBuffer createReadWrite(OpenCLContext context, long size) {
        return new OpenCLBuffer(context, size, CL.CL_MEM_READ_WRITE);
    }
    
    public static OpenCLBuffer createReadOnly(OpenCLContext context, long size) {
        return new OpenCLBuffer(context, size, CL.CL_MEM_READ_ONLY);
    }
    
    public static OpenCLBuffer createWriteOnly(OpenCLContext context, long size) {
        return new OpenCLBuffer(context, size, CL.CL_MEM_WRITE_ONLY);
    }
    
    public void write(float[] data) {
        if (data == null || data.length * Sizeof.cl_float > size) {
            throw new IllegalArgumentException("Data size exceeds buffer size");
        }
        
        CL.clEnqueueWriteBuffer(
            context.getCommandQueue(),
            buffer,
            CL.CL_TRUE,
            0,
            data.length * Sizeof.cl_float,
            Pointer.to(data),
            0,
            null,
            null
        );
    }
    
    public void write(double[] data) {
        if (data == null || data.length * Sizeof.cl_double > size) {
            throw new IllegalArgumentException("Data size exceeds buffer size");
        }
        
        CL.clEnqueueWriteBuffer(
            context.getCommandQueue(),
            buffer,
            CL.CL_TRUE,
            0,
            data.length * Sizeof.cl_double,
            Pointer.to(data),
            0,
            null,
            null
        );
    }
    
    public void write(int[] data) {
        if (data == null || data.length * Sizeof.cl_int > size) {
            throw new IllegalArgumentException("Data size exceeds buffer size");
        }
        
        CL.clEnqueueWriteBuffer(
            context.getCommandQueue(),
            buffer,
            CL.CL_TRUE,
            0,
            data.length * Sizeof.cl_int,
            Pointer.to(data),
            0,
            null,
            null
        );
    }
    
    public float[] readFloat(int length) {
        float[] data = new float[length];
        
        CL.clEnqueueReadBuffer(
            context.getCommandQueue(),
            buffer,
            CL.CL_TRUE,
            0,
            length * Sizeof.cl_float,
            Pointer.to(data),
            0,
            null,
            null
        );
        
        return data;
    }
    
    public double[] readDouble(int length) {
        double[] data = new double[length];
        
        CL.clEnqueueReadBuffer(
            context.getCommandQueue(),
            buffer,
            CL.CL_TRUE,
            0,
            length * Sizeof.cl_double,
            Pointer.to(data),
            0,
            null,
            null
        );
        
        return data;
    }
    
    public int[] readInt(int length) {
        int[] data = new int[length];
        
        CL.clEnqueueReadBuffer(
            context.getCommandQueue(),
            buffer,
            CL.CL_TRUE,
            0,
            length * Sizeof.cl_int,
            Pointer.to(data),
            0,
            null,
            null
        );
        
        return data;
    }
    
    public cl_mem getBuffer() {
        return buffer;
    }
    
    public long getSize() {
        return size;
    }
    
    public void cleanup() {
        if (buffer != null) {
            CL.clReleaseMemObject(buffer);
            buffer = null;
        }
        logger.debug("OpenCL buffer cleaned up");
    }
}
