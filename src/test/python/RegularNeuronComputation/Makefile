# Makefile

TOPLEVEL_LANG = verilog
VERILOG_SOURCES += $(shell pwd)/../../../../generated/regular/RegularNeuronComputation.sv
VERILOG_SOURCES += $(shell pwd)/../../../../generated/regular/MultipleComparator.sv
VERILOG_SOURCES += $(shell pwd)/../../../../generated/regular/Comparator.sv
TOPLEVEL = RegularNeuronComputation
MODULE = test_RegularNeuronComputation

include $(shell cocotb-config --makefiles)/Makefile.sim