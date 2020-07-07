library(ggplot2)

#csv files must be in same directory as r file
#must set working directory to r file location
#setwd()
blind <- read.csv("2010Blind.csv") # individuals updated based on team outcome,
plays <- read.csv("2010Plays.csv") # individuals updated by play-by-play outcome
combo <- read.csv("2010Combo.csv") # combo approach
name <- "Huston Street"
v1 <- blind[blind[,1]==name,3:ncol(blind)] #blue
v2 <- plays[plays[,1]==name,3:ncol(plays)] #red
v3 <- combo[combo[,1]==name,3:ncol(combo)] #purple
#v2 <- blind[blind[,1]=="Micah Hoffpauir",3:205]
plot(unlist(v1), type="l", col="blue", ylim=c(min(min(v1),min(v2),min(v3)), max(max(v1),max(v2),max(v3))))
lines(unlist(v2), type="l", col="red")
lines(unlist(v3), type="l", col="purple")
#plot(`2010Blind`[,3:205],type = "l")
colnames(`2010Combo`)
colnames(`2010Plays`)
colnames(`2010Blind`)
