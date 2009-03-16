rm(list=ls())
require(Design)
data = read.csv('pred.csv')
p = vector(length = 1)
var = vector(length = 1)
coeff = vector(length = 1)
countyn = vector(length = 1)
county = vector(length = 1)

analyze <- function(response, prediction, count) {
  data <- subset(data.frame(response, prediction, count), response == 'y' | response == 'n')
  countyn <- length(data$response)
  county <- length(data$response[data$response == 'y'])
  if ((length(data$response[data$response == 'y']) == 0) || (mean(data$prediction) == 0)) {
    return (c(countyn, county, 1, 0))
  }

  m <- 0

  ## lrm is in fact the wrong model: the probability of the response
  ## is not a logit of the prediction + count; instead it should be
  ## proportional to the count
  ## data$pred <- min(0.9, max(0.1, data$prediction))
  ## data$odds <- log((data$pred) / (1 - data$pred))
  ## try(m <- lrm(response == 'y' ~ prediction + count, data=data))
  ## if (!is.list(m)) {
  ##  return (c(1, 0))
  ## }
  ## return (c(anova(m)[1,3],  coefficients(m)[2]))
  ## m <- lrm(response == 'y' ~ prediction, data=data)
  ## return (c(anova(m)[1,3],  coefficients(m)[2]))
  m <- lm(response == 'y' ~ prediction + rcs(count), data=data)
  summary(m)
  return (c(countyn, county, anova(m)[1,5], m$coefficients[2]))
}

for (i in 0:((length(colnames(data))-5)/2 - 1)) {
  response <- colnames(data)[6 + i*2]
  prediction <- colnames(data)[6 + i*2 + 1]
  print(paste(response,prediction))
  analysis <- analyze(data[[response]], data[[prediction]], data$count)
 #if (analysis[4] > 0) {
    var[i + 1] <- response
    countyn[i + 1] <- analysis[1]
    county[i + 1] <- analysis[2]
    p[i + 1] <- analysis[3]
    coeff[i + 1] <- analysis[4]
 #}    
}

padjusted <- p.adjust(p, method="fdr")
var[padjusted < 0.05 & coeff > 0 & !is.na(var)]
padjusted[padjusted < 0.05 & coeff > 0 & !is.na(var)]

# ideally should be empty 
var[padjusted < 0.05 & coeff < 0 & !is.na(var)]

result <- as.data.frame(list(var = var, countyn = countyn, county = county, padjusted = padjusted))
#result2 <- as.data.frame(list(var = resultarticle$var[1:20],countyn = resultarticle$countyn[1:20],county=resultarticle$county[1:20],p = resultarticle$p[1:20],var2 = c(as.vector(resultarticle$var[21:39]),NA),countyn2 = c(resultarticle$countyn[21:39], NA),county2 = c(resultarticle$county[21:39],NA),p2=c(resultarticle$padjusted[21:39],NA)))