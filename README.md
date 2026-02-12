# CSCH Framework

[![Java](https://img.shields.io/badge/Java-17+-orange.svg)](https://openjdk.org/)
[![Maven](https://img.shields.io/badge/Maven-3.8+-blue.svg)](https://maven.apache.org/)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

**Cognitive-Subconscious-Cerebellar Hierarchy Framework** - A human brain-inspired architecture for intelligent agent control. 

## Overview

CSCH Framework is a Java-based intelligent control framework inspired by the hierarchical structure of the human brain. It implements a three-layer cognitive architecture that mimics how humans process intentions, translate them into skills, and execute precise motor actions.

```
┌─────────────────────────────────────────────────────────────┐
│                    Conscious Layer                          │
│     Goal Planning → Intent Generation → Reflection          │
│                   (High-Level Reasoning)                    │
├─────────────────────────────────────────────────────────────┤
│                   Subconscious Layer                        │
│    Intent Translation → Skill Selection → Failure Recovery  │
│                   (Mid-Level Processing)                    │
├─────────────────────────────────────────────────────────────┤
│                    Cerebellum Layer                         │
│   Motor Action Computation → PID Control → Action Smoothing │
│                   (Low-Level Execution)                     │
├─────────────────────────────────────────────────────────────┤
│                     Safety Shield                           │
│        Rule-Based Action Filtering → Violation Detection    │
└─────────────────────────────────────────────────────────────┘
```

## Architecture

### Three-Layer Hierarchy

| Layer | Responsibility | Frequency |
|-------|---------------|-----------|
| **Conscious** | Goal decomposition, intent graph generation, strategic intervention | Low (event-driven) |
| **Subconscious** | Intent-to-skill translation, skill selection, execution monitoring | Medium |
| **Cerebellum** | Motor action computation, smooth control, feedback learning | High (20-100 Hz) |

### Core Components

- **Intent Graph** - DAG structure representing decomposed goals and their dependencies
- **Skill System** - Parameterized, reusable action templates with execution history
- **Safety Shield** - Rule-based action filtering with multiple safety rules
- **Event Bus** - Asynchronous event-driven communication between components
- **State Provider** - World state abstraction for environment perception

## Modules

```
CSCH-Framework/
├── csch-core/           # Core interfaces, data structures, and base types
├── csch-conscious/      # Conscious layer implementation
├── csch-subconscious/   # Subconscious layer implementation
├── csch-cerebellum/     # Cerebellum layer with PID control
├── csch-safety/         # Safety shield and rule implementations
├── csch-opencl/         # OpenCL-based neural network acceleration
├── csch-api/            # High-level API and factory classes
└── csch-integration/    # Integration tests and examples
```

## Features

- **Hierarchical Control** - Three-layer architecture inspired by human brain
- **Intent-Based Planning** - Goal decomposition into executable intent graphs
- **Skill System** - Reusable, parameterized skill definitions with categories
- **Safety First** - Built-in safety rules for cliff avoidance, health protection, hazard avoidance
- **Smooth Control** - PID-based motor control with action smoothing
- **Event-Driven** - Asynchronous event bus for component communication
- **OpenCL Acceleration** - Optional GPU acceleration for neural network computations
- **Extensible** - Clean interfaces for custom layer implementations

## Quick Start

### Prerequisites

- Java 17 or higher
- Maven 3.8+

### Installation

```bash
git clone https://github.com/lytharalab/csch-framework.git
cd csch-framework
mvn clean install
```

### Basic Usage

```java
import org.lytharalab.csch.api.*;
import org.lytharalab.csch.core.config.CSCHConfiguration;

// Create state provider for your environment
StateProvider stateProvider = new YourStateProvider();

// Configure the system
CSCHConfiguration config = CSCHConfiguration.builder()
    .controlFrequencyHz(20)
    .safetyShieldEnabled(true)
    .openclEnabled(false)
    .build();

// Create and initialize agent
CSCHAgent agent = CSCHFactory.createAgent(stateProvider, config);
agent.initialize();
agent.start();

// Set a goal
agent.executeGoal("Hurry up and dig for stones");

// Get motor actions in control loop
while (running) {
    MotorAction action = agent.getAction(100, TimeUnit.MILLISECONDS);
    if (action != null) {
        // Execute action in your environment
        executeAction(action);
        
        // Report success/failure
        agent.reportSuccess();
    }
}

// Shutdown
agent.stop();
agent.shutdown();
```

## Safety Rules

The framework includes several built-in safety rules:

| Rule | Description | Severity |
|------|-------------|----------|
| `CliffAvoidanceRule` | Prevents falling from heights | CRITICAL |
| `HealthProtectionRule` | Protects player health | HIGH |
| `HazardAvoidanceRule` | Avoids environmental hazards | HIGH |
| `CombatSafetyRule` | Safe combat behavior | MEDIUM |
| `ActionRateLimitRule` | Limits action frequency | LOW |

## Configuration Options

```java
CSCHConfiguration config = CSCHConfiguration.builder()
    .controlFrequencyHz(20)        // Control loop frequency
    .safetyShieldEnabled(true)     // Enable safety filtering
    .openclEnabled(false)          // Enable GPU acceleration
    .maxIntentDepth(10)            // Maximum intent graph depth
    .skillTimeoutMs(30000)         // Skill execution timeout
    .build();
```

## Extending the Framework

### Custom Safety Rule

```java
public class CustomSafetyRule implements SafetyRule {
    @Override
    public boolean violates(MotorAction action, WorldState state) {
        // Check if action violates safety
        return false;
    }
    
    @Override
    public MotorAction correct(MotorAction action, WorldState state) {
        // Return corrected action
        return action;
    }
}
```

### Custom Skill Definition

```java
SkillDefinition skill = SkillDefinition.builder()
    .name("customSkill")
    .description("A custom skill")
    .category(SkillCategory.MOVEMENT)
    .addParameter(SkillParameter.builder()
        .name("targetX")
        .type(Double.class)
        .required(true)
        .build())
    .estimatedDuration(Duration.ofSeconds(5))
    .interruptible(true)
    .build();
```

## Dependencies

- **SLF4J + Logback** - Logging
- **Jackson** - JSON serialization
- **Lombok** - Boilerplate reduction
- **Guava** - Utilities
- **Eclipse Collections** - High-performance collections
- **JOCL** (optional) - OpenCL bindings for GPU acceleration
- **JUnit 5 + Mockito** - Testing

## Project Structure

```
csch-core/
├── action/          # Motor action definitions
├── common/          # Common interfaces and types
├── config/          # Configuration classes
├── event/           # Event system
├── intent/          # Intent graph structures
├── layer/           # Layer interfaces
├── skill/           # Skill system
└── state/           # State providers

csch-safety/
├── rules/           # Safety rule implementations
└── SimpleSafetyShield.java
```

## Contributing

Contributions are welcome! Please feel free to submit issues and pull requests.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

This framework is inspired by neuroscience research on the hierarchical organization of the human brain, particularly the roles of the cerebral cortex (conscious processing), basal ganglia (subconscious skill selection), and cerebellum (motor control refinement).

---

**Lythara Lab** - Building intelligent systems inspired by nature.
