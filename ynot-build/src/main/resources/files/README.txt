 __  ____ _____________
 \ \/ / |/ / __ |_  __/
  \  /    / /_/ // /   
  /_/_/|_/\____//_/

Copyright (C) 2011 Eric Quesada

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>


**Build:

cd 
mkdir git 
cd git 
git clone git://github.com/ynotscript/ynot.git
cd ynot
mvn package 

**Install & Test:

cd 
mv git/ynot/ynot-build/target/ynot-xxx.zip . 
unzip ynot-xxx.zip 
cd ynot 
java -jar ynot-vm-xxx.jar ./examples/simpleTest.ynot