library(ggplot2)

#csv files must be in same directory as r file
#must set working directory to r file location
setwd("~/Research/RemoteRepos/baseball-elo/")
blind <- read.csv("2010sTrunc.csv") # individuals updated based on team outcome,
plays <- read.csv("2010sPlayByPlay.csv") # individuals updated by play-by-play outcome
combo <- read.csv("2010sCombo.csv") # combo approach
name <- "Cody Bellinger"
maxi <- 1
i <- 200
nrow(combo)
nrow(blind)
nrow(plays)
#blind[blind[,1]==name,1:ncol(blind)]
#for (i in 1:nrow(blind)) {
#blind[,1] <- factor(blind[,1], levels=levels(plays[,1]))
blind[,1] <- factor(blind[,1], levels=levels(plays[,1]))
combo[,1] <- factor(combo[,1], levels=levels(plays[,1]))
for (i in 1:maxi) {
  #name <- blind[,1][i]
  print(name)
  
  v1 <- blind[as.character(blind[,1])==name,5:ncol(blind)] #blue
  #print(v1)
  v2 <- plays[plays[,1]==name,5:ncol(plays)] #red
  v3 <- combo[combo[,1]==name,5:ncol(combo)] #purple
  #v2 <- blind[blind[,1]=="Micah Hoffpauir",3:205]
  #v1 <- blind[i,3:ncol(blind)] #blue
  #v2 <- plays[i,3:ncol(plays)] #red
  #v3 <- combo[i,3:ncol(combo)] #purple
  print(min(v1))
  #min(v2)
  #min(v3)
  #max(v1)
  #max(v2)
  #max(v3)
  plot(unlist(v1), type="l", col="blue", ylim=c(min(min(v1),min(v2),min(v3)), max(max(v1),max(v2),max(v3))), ylab="Rating", xlab="# of Games", main=name)
  lines(unlist(v2), type="l", col="red")
  lines(unlist(v3), type="l", col="purple")
}
#plot(`2010Blind`[,3:205],type = "l")
#colnames(blind)
#colnames(plays)
#colnames(combo)
