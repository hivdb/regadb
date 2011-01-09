# inter.csv is the inter-patient file 
# type;diversity
inter <- read.csv("inter.csv", sep=";")
inter <- subset(inter, inter$type == "O")
rows <- min(100000, length(inter$type))
inter <- inter[1:rows,]

# intra.csv: intra-patient file
# type;diversity
intra <- read.csv("intra.csv");
intra <- subset(intra, intra$type == "I")

expintra <- exp(intra$diversity)
expinter <- exp(inter$diversity)

intrad <- intra$diversity
interd <- inter$diversity

fitdistr(intrad[intrad > 0], "log-normal")

fitdistr(interd[interd > 0], "log-normal")