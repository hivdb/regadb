args = commandArgs(trailingOnly = TRUE)

inter_file <- ""
intra_file <- ""

for (i in 1:length(args)) {
  if (args[i] == "--inter" && i < length(args))
    inter_file <- args[i+1]
  if (args[i] == "--intra" && i < length(args)) 
    intra_file <- args[i+1]
}

if (inter_file == "" || intra_file == "") {
  cat("Expecting arguments: --inter inter.csv --intra intra.csv\n")
  quit(save='no')
}

# inter.csv is the inter-patient file 
# type,diversity
inter <- read.csv(inter_file, sep=",")
inter <- subset(inter, inter$type == "O")
inter <- inter[1:min(100000, length(inter$type)),]

# intra.csv: intra-patient file
# type,diversity
intra <- read.csv(intra_file, sep=",");
intra <- subset(intra, intra$type == "I")
intra <- intra[1:min(100000, length(intra$type)),]

intrad <- intra$diversity
interd <- inter$diversity

library (MASS)

print("intra log normal parameters")
fitdistr(intrad[intrad > 0], "log-normal")
print("inter log normal parameters")
fitdistr(interd[interd > 0], "log-normal")
