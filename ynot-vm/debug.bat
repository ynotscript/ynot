rmdir config /S /Q 
rmdir dictionaries /S /Q 
rmdir lib /S /Q 
echo y | del .\syntaxTest.ynot
echo r | xcopy /e ..\ynot\config .\config
echo r | xcopy /e ..\ynot\dictionaries .\dictionaries
echo r | xcopy /e ..\ynot\lib .\lib
copy ..\ynot\examples\syntaxTest.ynot .