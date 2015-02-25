startrek-clojure
================

A re-implementation of the old [Star Trek 1971](http://www.codeproject.com/Articles/28228/Star-Trek-Text-Game)
game in clojure.

My goal was to re-create this old game in a functional way and learn clojure at the same time. I have
learned a great deal about the language as part of this effort. However, there are several places I
am still unclear.

* I wrote 0 macros for this app. I feel like there should have been several opportunities to write some
useful macros. Any suggestions would be welcome.

* I have the state of the entire game stored in a single atom. I pass that global reference as a
parameter to every top level function. Every update to state occurs in a swap!. I rarely
found opportunities to "batch up the swap! calls nor do I understand if that would be desirable.

* Overall code flow still feels odd. I use the basic command pattern to orchestrate the functions. I
often find that I ignore the return value and rely on updating the global state. Note sure what best
clojure practice should be.

* I use let often. Variables defined in lets are everything from local caches of global state to
simple calculations that I use often in a method. Is that a good practice? Or should I take a
different approach?

* I find that my clojure code feels more verbose than it should be. I am looking for suggestions
to reduce the amount of code written to accomplish the same functionality.
