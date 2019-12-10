This has been forked from https://github.com/mortennobel/java-image-scaling which is currently unsupported.

Image Scaling Library for Java
--------------------------------

The purpose of the library is to provide better image scaling options
than the Java runtime provides.

Copyright 2013 Morten Nobel-Joergensen

The library is distributed under the BSD 3-Clause License ( http://opensource.org/licenses/BSD-3-Clause )

Version 8.6.7
-------------

* ResampleOp can now use an external ExecutorService, if not provided a default executor service will be started
  which caches threads.