import sys

from xml.dom.ext.reader import PyExpat
from xml.xpath import Evaluate

subtype = "//conclusion/assigned/name"

subtype_file = open(sys.argv[2],'w');

dom = PyExpat.Reader().fromUri(sys.argv[1])

elements = Evaluate(subtype, dom.documentElement)

for element in elements:
  	subtype_file.write(element.childNodes[0].data)

subtype_file.close()
