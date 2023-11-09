# RMMAT - Representativeness Measure for Multiple Aspect Trajectories

## Introduction

RMMAT (Representativeness Measure for Multiple Aspect Trajectories) is a Java-based project designed to compute and analyze the representativeness of a given representative trajectory (RT) within a dataset of multiple aspect trajectories. It calculates various statistical measures and provides insights into the quality of the RT in representing the dataset.

** This branch refers to the Source Code of RMMAT.

## Table of Contents

- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Usage](#usage)
- [Examples](#examples)
- [Contributing](#contributing)
- [Acknowledgments](#acknowledgments)
- [Paper](#paper)
- [Contact](#contact)


### Prerequisites

Before using RMMAT, make sure you have the following prerequisites installed:

- Java Development Kit (JDK)
- Input dataset and representative trajectory data

### Installation

To use RMMAT, follow these steps:

1. Clone this repository:

   ```bash
   git clone https://github.com/yourusername/RMMAT.git

2. Modify the source code in "RMMAT.java" to match the input file format of both the RT and input dataset
3. Create a Main class to run RMMAT
4. Compile the Main Java code
5. Run the program:
     ```bash
     java RMMAT


### Usage

RMMAT provides methods to compute representativeness measures for a given representative trajectory (RT) and an input dataset of multiple aspect trajectories. 
It is important to modify the source code as per the input file format of both the RT and input dataset.

package "measure" contains the main code regarding representativeness measure:
- RMMAT.java: This class calculates statistical measures and provides insights into the quality of the RT in representing the dataset
- LoadData.java: This class handles loading and processing input data for the RMMAT project
- MUITAS.java: The similarity measure used in this implementation in order to provide each similarity measure between the RT and each trajectory in input dataset.

### Examples

Here are some usage examples of **RMMAT**:

```java
// Create an instance of RMMAT
RMMAT rm = new RMMAT("directory_path", "file", inputTrajectories, representativeTrajectory);

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
We would also like to acknowledge the following funding and support:

- CAPES - Finance Code 001
- SoBigData++ Project - by TNA
- EU's Horizon 2020 research and innovation programme under GA N. 777695 (EU Project MASTER)
- Special thanks to the transnational visiting support in CNR from Pisa for access to the TagMyDay dataset
- Universidade Federal de Santa Catarina (UFSC), Ph.D. program in Computer Science

The views expressed are the authors' responsibility and do not necessarily reflect the views of the European Commission.

### Paper

This paper was accepted and will be available at the Brazilian Symposium on Geoinformatics (GEOINFO) of 2023.


### Contact & Contributing

For questions or feedback, you can contact the project maintainer at vanessalagomachado@gmail.com.

We welcome contributions from the community. Whether you want to report a bug, request a new feature, or contribute code, your involvement is appreciated, please contact the project maintainer.

Happy analyzing with RMMAT!


