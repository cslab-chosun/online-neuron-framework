# Makefile

TOPLEVEL_LANG = verilog
VERILOG_SOURCES += $(shell pwd)/../../../../generated/online/OnlineNeuronComputation.sv
VERILOG_SOURCES += $(shell pwd)/../../../../generated/online/OnlineMultipleComparator.sv
VERILOG_SOURCES += $(shell pwd)/../../../../generated/online/OnlineComparator2.sv
TOPLEVEL = OnlineNeuronComputation
MODULE = test_OnlineNeuronComputation

include $(shell cocotb-config --makefiles)/Makefile.sim