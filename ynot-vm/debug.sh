#!/bin/sh
unlink config
unlink dictionaries
unlink lib
unlink syntaxTest.ynot
ln -s ../ynot/config
ln -s ../ynot/dictionaries
ln -s ../ynot/lib
ln -s ../ynot/examples/syntaxTest.ynot
