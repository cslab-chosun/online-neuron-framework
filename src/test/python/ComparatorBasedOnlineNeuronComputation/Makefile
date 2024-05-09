# Makefile

TOPLEVEL_LANG = verilog
VERILOG_SOURCES += $(shell pwd)/../../../../generated/online/comparator/ComparatorBasedOnlineNeuronComputation.sv
VERILOG_SOURCES += $(shell pwd)/../../../../generated/online/comparator/OnlineMultipleComparator.sv
VERILOG_SOURCES += $(shell pwd)/../../../../generated/online/comparator/OnlineComparator2.sv
TOPLEVEL = ComparatorBasedOnlineNeuronComputation
MODULE = test_ComparatorBasedOnlineNeuronComputation

include $(shell cocotb-config --makefiles)/Makefile.sim