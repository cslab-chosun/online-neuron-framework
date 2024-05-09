# Makefile

TOPLEVEL_LANG = verilog
VERILOG_SOURCES += $(shell pwd)/../../../../generated/regular/comparator/ComparatorBasedRegularNeuronComputation.sv
VERILOG_SOURCES += $(shell pwd)/../../../../generated/regular/comparator/MultipleComparator.sv
VERILOG_SOURCES += $(shell pwd)/../../../../generated/regular/comparator/Comparator.sv
TOPLEVEL = ComparatorBasedRegularNeuronComputation
MODULE = test_ComparatorBasedRegularNeuronComputation

include $(shell cocotb-config --makefiles)/Makefile.sim
