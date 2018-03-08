# DNA Reservoir
## How to use
Clone repository and set up submodules.
```
$ git clone git@hagi.is.s.u-tokyo.ac.jp:wtr816/DNA-reservoir.git
$ cd DNA-reservoir
$ git submodule init
$ git submodule update
```
Generate eclipse projects.
```
$ make eclipse
```
Open all repositories `DNA-reservoir`, `DACCAD` and `bioneat` in eclipse.
The main class is `evo.RunReservoir` and some examples are contained in `cases` directory.
You can run an example by giving following command line arguments from `Run > Run Configuration..`.
```
-c ../cases/ERNe_TaskB/config.json
```
You can also view logs by giving the following arguments.
```
-r path_to_result_dir/
```
