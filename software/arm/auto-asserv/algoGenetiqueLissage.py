# -*- coding: Utf-8 -*-
__author__ = 'Clément'

"""
Ce programme a été développé dans le cadre du cours CSC4504
Le problème est de trouver l'équilibre sur une balance sur laquelle on a placé un poids quelconque et à l'aide de poids
qui ont pour particularité d'être une puissance de 3.
La solution sera ici trouvée (ou approchée) par un algorithme évolutionnaire. Les paramètres du problèmes sont :
-la taille maximale des individus
-le nombre de génération
-le nombre d'individu dans la population
"""
import random
import math
import numpy as np
import math
import sys #pour avoir le nombre maximal autorisé

class LisseurGenetique:
    def __init__(self, mesures, duree, nPop, nAperiodique, generation, taux):
        self.MESURES = np.array(mesures)
        self.DUREE = duree
        self.population = []
        self.nPop = nPop
        self.nAperiodique = nAperiodique
        self.generation = generation
        self.taux = taux
        self.__genererPopulation()
        self.__evoluer()

    def __creer_liste(self, taille):
        """
        Retourne une liste de taille taille
        :param taille:
        :return l
        """
        l = []
        for i in range(taille):
            l.append(None)
        return l

    def __genererPopulation(self):
        """
        :param nPop:
        :return: popu  #la population
        """
        self.population = [] #liste des individus
        self.nPseudoPeriodique = self.nPop - self.nAperiodique
        for i in range(self.nPseudoPeriodique):
            self.population.append([])
            self.population[i].append(math.pow(10, random.random()*12-6))
            self.population[i].append(math.pow(10, random.random()*12-6))
            self.population[i].append(math.pow(10, random.random()*12-6))
            self.population[i].append(math.pow(10, random.random()*12-6))
            self.population[i].append("P") # "P" pour pseudo-périodique
        for i in range(self.nAperiodique):
            self.population.append([])
            self.population[i].append(math.pow(10, random.random()*12-6))
            self.population[i].append(math.pow(10, random.random()*12-6))
            self.population[i].append(math.pow(10, random.random()*12-6))
            self.population[i].append(math.pow(10, random.random()*12-6))
            self.population[i].append("A") # "A" pour apériodique

    def __f_aperiodique(self, beta, alpha, A, B, t):
        return math.exp(-beta*t)*(A*math.exp(alpha*t)+B*math.exp(alpha*t))

    def __f_pseudo_periodique(self, beta, omega, A, B, t):
        return  math.exp(-beta*t)*(A*math.cos(omega*t)+B*math.sin(omega*t))

    def __evaluer(self, individu):
        """
        :param individu: c'est lui qu'on évalue
        :param mesure: c'est la mesure
        :param duree : c'est la durée de la mesure
        :return ecart: c'est l'écart par rapport à l'équilibre
        """
        Nmesures = len(self.MESURES)
        mesures = np.array(self.MESURES)
        temps_modele = np.linspace(0, self.DUREE, Nmesures)
        beta = individu[0]
        omega = individu[1]
        A = individu[2]
        B = individu[3]
        if individu[4] == "P":
            valeurs_modele = np.array([self.__f_pseudo_periodique(beta, omega, A, B, t) for t in temps_modele])
        elif individu[4] == "A":
            valeurs_modele = np.array([self.__f_aperiodique(beta, omega, A, B, t) for t in temps_modele])
        else:
            raise ValueError
        diff = mesures - valeurs_modele
        ecart  = np.dot(diff,diff.T)
        return ecart

    def __crossover(self, parent1, parent2, mesures, duree):
        """
        On crée les enfants par un simple crossingover si les parents entourent l'équilibre
        Si les deux parents sont d'un côté de l'équilibre, alors on garde celui qui est le plus proche de l'équilibre
        Cette fonction contient la reproduction, la sélection et le croisement
        :param parent1:
        :param parent2:
        :return:
        """
        #On duplique le meilleur des parents
        ev1 = self.__evaluer(parent1)
        ev2 = self.__evaluer(parent2)
        if ev1 < ev2:
            enfant1 = parent1
            enfant2 = []
            enfant2[0] = (3*parent1[0]+parent2[0])/4.
            enfant2[1] = (3*parent1[1]+parent2[1])/4.
            enfant2[2] = (3*parent1[2]+parent2[2])/4.
            enfant2[3] = (3*parent1[3]+parent2[3])/4.
            enfant2[4] = parent1[4]
        else:
            enfant1 = parent2
            enfant2 = []
            enfant2[0] = (parent1[0]+3*parent2[0])/4.
            enfant2[1] = (parent1[1]+3*parent2[1])/4.
            enfant2[2] = (parent1[2]+3*parent2[2])/4.
            enfant2[3] = (parent1[3]+3*parent2[3])/4.
            enfant2[4] = parent2[4]
        return enfant1, enfant2

    def __mutation(self, individu, taux):
        """
        La mutation permet de découvrir d'autres solutions possibles
        :param individu:
        :param taux: permet d'ajuster le nombre de mutation
        :return:
        """
        if random.random < taux:
            individu[random.randrange(4)] = math.pow(10,random.random()*12-6)
            return individu

    def __evoluer(self):
        #Les individus sont enregistrés dans population
        individus = self.population
        minTot = sys.maxint
        for i in range(self.generation):
            population = []
            random.shuffle(individus) #On mélange aléatoirement les individus dans la liste
            for j in range(self.nPop/2):
                #La reproduction se fait avec deux individus proches dans la liste individus
                a, b = self.__crossover(individus[j], individus[j+1])
                #print a,b, "parent enfant"
                population.append(self.__mutation(a, 0.2))
                population.append(self.__mutation(b, 0.2))
            #print population
            individus = population
            """
            #On trie pour garder le résultat le plus intéressant
            mini = self.__evaluer(population[0], self.MESURES)
            chemin_choisi = 0
            for i in range(1, nPop):
                if mini > self.__evaluer(population[i], self.MESURES):
                    chemin_choisi = i
                    mini = self.__evaluer(population[i], self.MESURES)
            #min est le chemin qui a la plus petite évaluation
            #A chaque fois qu'on a un meilleur résultat, on l'affiche
            if minTot > mini:
                minTot = mini
                print population[chemin_choisi], minTot
            if mini == 0:
                break
            """
        self.population = population

    def meilleurs_resultats(self, n):
        for i in range(n):
            mini = i
            #Achtung, hier muss i+1 weniger als n-1 sein !
            for j in range(i+1,self.nPop):
                if self.population[j] < self.population[mini]:
                    mini = j
            if i != mini:
                aux = self.population[i]
                self.population[i] = self.population[mini]
                self.population[mini] = aux
        return self.population[:n]


if __name__ == "__main__":
    pass
    #-----------------------------------
    #mesures
    mesures = None
    #durée en ms
    duree = 1000
    #nombre de génération
    generation = 10
    #nombre d'individus dans la population
    nPop = 100
    #nombre de fonction apériodique
    nAperiodique = 50
    #nombre de génération
    nGeneration = 200
    #taux de mutation
    taux = 0.2
    #nombre de meilleur résultat voulu
    nbResultat = 5
    #----------------------------------
    lisseur = LisseurGenetique(mesures, duree, nPop, nAperiodique, nGeneration, taux)
    lisseur.meilleurs_resultats(nbResultat)
    #C'est ici qu'on récupère la valeur à donner

