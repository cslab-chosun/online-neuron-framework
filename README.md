<p align="center">
    <img alt="Online Neuron Framework" title="Online Neuron Framework" src="https://raw.githubusercontent.com/cslab-chosun/uploaded-files/main/online-neuron-framework-logo/png/logo-no-background.png" width="400">
</p>

<p align="center">
  <b>Online/Conventional Arithmetic Neuron Framework</b>
</p>

## Introduction

This repository contains source code used for comparing high-level results corresponding to Conventional (CA) and Online (OA) arithmetic methodologies across different input sizes. The comparison is based on Directed Acyclic Graphs (DFGs) derived from dense layers of Deep Neural Networks (DNNs) such as AlexNet and VGG.

<p align="center">
  <img alt="" title=""  src="https://raw.githubusercontent.com/cslab-chosun/uploaded-files/main/img/neuron-1.png" width=40%><img alt="" title=""  src="https://raw.githubusercontent.com/cslab-chosun/uploaded-files/main/img/fsmr-1.png" width=40%>
</p>

## Methodology
DFGs are utilized to represent neurons with varying numbers of inputs: 32, 64, 128, 256, 512, 1024, 2048, and 4096. Each neuron computation involves a dot product operation followed by a Rectified Linear Unit (ReLU) activation function.

## Hardware Platform
The final synthesis results are performed using Xilinx Vivado and a Zynq UltraScale+ MPSoC ZCU104 XCZU7EV FPGA.

## Output 

Output files are already generated and available in this repo's `output` directory.

## Generating Output Manually
For generating SystemVerilog files, you need to install [Chisel](https://www.chisel-lang.org/docs/installation). Once installed, use the following commands:

```sh
$ sbt run
```

This command prompts you to select a component. You can select one of the following components based on your specific computation needs (**conventional**/**online**):

```sh
$ sbt run
[info] welcome to sbt 1.9.7 (Eclipse Adoptium Java 17.0.11)
[info] loading settings for project online-neuron-framework-build from plugins.sbt ...
[info] loading project definition from /home/online-neuron-framework/project
[info] loading settings for project root from build.sbt ...
[info] set current project to online-neuron-framework (in build file:/home/online-neuron-framework/)

Multiple main classes detected. Select one to run:
 [1] neuron.MainAdditionBasedOnlineCircuitGenerator
 [2] neuron.MainAdditionBasedRegularCircuitGenerator
 [3] neuron.MainComparatorBasedOnlineCircuitGenerator
 [4] neuron.MainComparatorBasedRegularCircuitGenerator
 [5] neuron.components.MainOnlineAdderGenerator
 [6] neuron.components.MainOnlineMultiplierGenerator
```

The generated code for either online or conventional arithmetic can be found in the `generated` directory.

## Testbenches

To test the framework, [cocotb](https://www.cocotb.org/) should be installed. After that, first, run the framework generator code (generated SystemVerilog files) and then run the following commands:

```sh
cd src/test/python/AdditionBasedRegularNeuronComputation
./test.sh
```

The above command generates a waves file at `./src/test/python/AdditionBasedRegularNeuronComputation/AdditionBasedRegularNeuronComputation.fst` which can be read using [GTKWave](https://gtkwave.sourceforge.net/).

```sh
cd src/test/python/AdditionBasedRegularNeuronComputation
gtkwave ./sim_build/AdditionBasedRegularNeuronComputation.fst
```

## API

If you want to create the latest version of API documentation, you can run the following command:

```sh
$ sbt doc
```

This will generate documentation at `./target/scala-{version}/api/index.html`.

## License

**Online Neuron Framework** is licensed under **GPLv3** LICENSE.
