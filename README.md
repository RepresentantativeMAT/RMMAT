# RMMAT - Representativeness Measure for Multiple Aspect Trajectories

## Introduction

RMMAT (Representativeness Measure for Multiple Aspect Trajectories) is a Java-based project designed to compute and analyze the representativeness of a given representative trajectory (RT) within a dataset of multiple aspect trajectories. It calculates various statistical measures and provides insights into the quality of the RT in representing the dataset.

## Table of Contents

- [Getting Started](#getting-started)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Usage](#usage)
- [Examples](#examples)
- [Contributing](#contributing)
- [Acknowledgments](#acknowledgments)

## Getting Started

### Prerequisites

Before using RMMAT, make sure you have the following prerequisites installed:

- Java Development Kit (JDK)
- Input dataset and representative trajectory data

### Installation

To use RMMAT, follow these steps:

1. Clone this repository:

   ```bash
   git clone https://github.com/yourusername/RMMAT.git

2. Compile the Java code:
    ```bash
   javac RMMAT.java
    
4. Run the program:
     ```bash
     java RMMAT


### Usage

RMMAT provides methods to compute representativeness measures for a given representative trajectory (RT) and an input dataset of multiple aspect trajectories. 

### Examples

Here are some usage examples of **RMMAT**:

```java
// Create an instance of RMMAT
RMMAT rm = new RMMAT("directory", "file", inputTrajectories, representativeTrajectory);

// Compute representativeness measures
rm.computeRepresentativenessMeasure();
```

### Contributing
We would like to acknowledge the following contributors for their support and valuable input:

- Vanessa Lago Machado (vanessalagomachado@gmail.com)
- Tarlis Tortelli Portela
- Chiara Renso
- Ronaldo dos Santos Mello

### Acknowledgments
We would also like to acknowledge the following fundings and support:

- CAPES - Finance Code 001
- SoBigData++ Project - by TNA
- EU's Horizon 2020 research and innovation programme under GA N. 777695 (EU Project MASTER)
- Special thanks to the transnational visiting support, in CNR from Pisa, for access to the TagMyDay dataset
- Universidade Federal de Santa Catarina (UFSC), Ph.D. program in Computer Science

The views expressed are the authors' responsibility and do not necessarily reflect the views of the European Commission.

