# Makefile

TOPLEVEL_LANG = verilog
VERILOG_SOURCES += $(shell pwd)/../../../../generated/regular/addition/AdditionBasedRegularNeuronComputation.sv
VERILOG_SOURCES += $(shell pwd)/../../../../generated/regular/addition/MultipleMultiplicationAddition.sv
VERILOG_SOURCES += $(shell pwd)/../../../../generated/regular/addition/MultiplicationAndDivision.sv
VERILOG_SOURCES += $(shell pwd)/../../../../generated/regular/addition/AdditionAndSubtraction.sv
TOPLEVEL = AdditionBasedRegularNeuronComputation
MODULE = test_AdditionBasedRegularNeuronComputation

include $(shell cocotb-config --makefiles)/Makefile.sim
