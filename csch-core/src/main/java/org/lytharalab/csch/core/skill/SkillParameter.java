package org.lytharalab.csch.core.skill;

public class SkillParameter {
    private final String name;
    private final String description;
    private final Class<?> type;
    private final boolean required;
    private final Object defaultValue;
    private final double minValue;
    private final double maxValue;
    
    private SkillParameter(Builder builder) {
        this.name = builder.name;
        this.description = builder.description;
        this.type = builder.type != null ? builder.type : Object.class;
        this.required = builder.required;
        this.defaultValue = builder.defaultValue;
        this.minValue = builder.minValue;
        this.maxValue = builder.maxValue;
    }
    
    public String getName() { return name; }
    public String getDescription() { return description; }
    public Class<?> getType() { return type; }
    public boolean isRequired() { return required; }
    public Object getDefaultValue() { return defaultValue; }
    public double getMinValue() { return minValue; }
    public double getMaxValue() { return maxValue; }
    
    public boolean hasRange() {
        return minValue != Double.NEGATIVE_INFINITY || maxValue != Double.POSITIVE_INFINITY;
    }
    
    public boolean isValid(Object value) {
        if (value == null) {
            return !required || defaultValue != null;
        }
        if (!type.isInstance(value)) {
            return false;
        }
        if (hasRange() && value instanceof Number) {
            double numValue = ((Number) value).doubleValue();
            return numValue >= minValue && numValue <= maxValue;
        }
        return true;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private String name;
        private String description;
        private Class<?> type = Object.class;
        private boolean required = false;
        private Object defaultValue;
        private double minValue = Double.NEGATIVE_INFINITY;
        private double maxValue = Double.POSITIVE_INFINITY;
        
        public Builder name(String name) {
            this.name = name;
            return this;
        }
        
        public Builder description(String description) {
            this.description = description;
            return this;
        }
        
        public Builder type(Class<?> type) {
            this.type = type;
            return this;
        }
        
        public Builder required(boolean required) {
            this.required = required;
            return this;
        }
        
        public Builder defaultValue(Object defaultValue) {
            this.defaultValue = defaultValue;
            return this;
        }
        
        public Builder range(double min, double max) {
            this.minValue = min;
            this.maxValue = max;
            return this;
        }
        
        public SkillParameter build() {
            return new SkillParameter(this);
        }
    }
}
