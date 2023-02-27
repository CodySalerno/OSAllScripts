# OSAllScripts

Reminders to nuances due to scripting environment.

* use lots of if/else if/else statements, botting is prone to misclicks and lots of error detection and error correcting is necessary.

* utility classes MUST have a MethodProvider in their constructor;
this lets any functions inside the utility class call the base script class when a scripting function needs to be run.

* onLoop() is your main method that the script environment runs.
* onStart() is run only once when a script is started.
* Most methods will ned to throw InterruptedException to handle if the user stops the script while the function is run.
