# Cache-Simulator
A project that simulates processor cache operations within System Programming course.

## How It Works
The program uses dummy data as ram.txt. Instructions are given inside .trace files and the program's aim is to simulate cache operations (hit, miss etc.) for a processor. The final contents of the caches are printed inside "L1D Cache.txt" , "L1I Cache.txt" "L2 Cache.txt" files. Updated ram contents are printed to "ram updated.txt" and the log of operations are displayed in "log.txt".

## Usage
 ./talha_bayburtlu   -L1s <L1s> -L1E <L1E> -L1b <L1b> -L2s <L2s> -L2E <L2E> -L2b <L2b> -t <tracefile> 
  
• -L1Ds <L1Ds>: Number of set index bits for L1 data/instruction cache (S = 2s is the number of sets) \
• -L1DE <L1DE>: Associativity for L1 data/instruction cache (number of lines per set) \
• -L1Db <L1Db>: Number of block bits for L1 data/instruction cache (B = 2b is the block size) \
• -L2s <L2s>: Number of set index bits for L2 cache (S = 2s is the number of sets) \
• -L2E <L2E>: Associativity for L2 cache (number of lines per set) \
• -L2b <L2b>: Number of block bits for L2 cache (B = 2b is the block size) \
• -t <tracefile>: Name of the trace file (see reference trace files part on required notes) \

## Required Notes
• Each of the caches implements write-through and no write allocate mechanism for store and modify instructions.\
• For the evictions, FIFO (first in first out) policy used. \
• The format of each line for instructions : *operation address, size, data(only for M and S operations)* \
• In the trace files, “I” denotes an instruction load, “L” a data load, “S” a data store, and “M” a data modify (i.e., a data load followed by a data store). \
