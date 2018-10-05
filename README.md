# Eventropy - entropy estimation for sequential data

A fast CLI based tool implementing a number of entropy estimators for XES event logs and other sequential data.

## Usage

Download eventropy.jar. Run from the command line with:

`java -jar eventropy.jar [OPTIONS]... [FILE/PATH]...`

For a help text describing all options, use: `java -jar eventropy.jar -h` (see also below). For example, to compute the prefix entropy and the difference based entropy rate using constraint 5 on log.xes, and show progress:

`java -jar eventropy -sp -d 5 "log.xes"`

If the path to a directory is entered, eventropy will search for all relevant files in that directory and its subdirectories. Multiple paths to files or directories are allowed.

OPTIONS are one or more of the following entropy measures or other options. Options taking one or more parameters should have a space between the option and each parameter (e.g. `-knn 2 1`):

* `-h,--help` Print help message
* `-t,--time` Print time elapsed for computing metric
* `-s,--show-progress` Show progress in console (do not use if piping to output file)
* `-T,--timeout <SECONDS>` Set time limit for metric computation
* `-D,--significant-digits <DIGITS>` Set precision of output (no. of significant digits)
* `-F,--flatten` Ignore multiple occurrences of same sequence
* `-f,--frequency` Trace/sequence frequency based entropy
* `-p,--prefix` Prefix based entropy
* `-B,--k-block-global` Global _k_-block entropy
* `-b,--k-block <SIZE>` _k_-block entropy
* `-K,--kl <DIMENSION>` Kozachenko-Leonenko (nearest neighbor) entropy
* `-k,--knn <K, DIMENSION>` Entropy based on the _k_ th nearest neighbor`
* `-z,--lempel-ziv` Lempel-Ziv entropy rate
* `-d,--block-diff <CONST>` _k_-block entropy rate using difference-based estimate and cutoff constraint from 1-5
* `-r,--block-ratio <CONST>` _k_-block entropy rate using ratio-based estimate and cutoff constraint from 1-5
* `-u,--unique` Ratio of unique traces to total traces (0.0 - 1.0)
