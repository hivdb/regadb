#! /bin/sh

#dependencies
apt-get -y install cmake build-essential libboost-regex-dev libpopt-dev libxerces-c2-dev

#libseq
rm -rf /tmp/libseq
git clone git://libseq.git.sourceforge.net/gitroot/libseq/libseq /tmp/libseq
cd /tmp/libseq
cmake .
make install

#sequencetool
rm -rf /tmp/sequencetool
git clone http://regaweb.med.kuleuven.be/git/sequencetool /tmp/sequencetool
cd /tmp/sequencetool
mkdir build
cd build
cmake ../
make install -C src/lib

mkdir -p /soft/bin
cp /tmp/sequencetool/build/src/lib/* /soft/bin
