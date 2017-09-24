http://docs.opencv.org/3.3.0/d7/d9f/tutorial_linux_install.html

     $ cmake -DCMAKE_BUILD_TYPE=Release -DCMAKE_INSTALL_PREFIX=/usr/local -DBUILD_SHARED_LIBS= -DBUILD_EXAMPLES= -D BUILD_TESTS= -DBUILD_PERF_TESTS=  ~/Projects/github/opencv
     $ make -j4
     $ sudo make install

uninstall: nochmal cmake und dann make uninstall ?


     $ sudo apt install libcv2.4
     $ sudo apt install libopencv2.4-java
