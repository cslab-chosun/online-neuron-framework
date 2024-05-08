module cocotb_iverilog_dump();
initial begin
    $dumpfile("sim_build/RegularNeuronComputation.fst");
    $dumpvars(0, RegularNeuronComputation);
end
endmodule
