/**
 * \file main.cpp
 *
 * Fichier principal qui sert juste à appeler les fichiers, créer la structure Robot et faire le traitement du port série
 */

#include <util/delay.h>
#include <avr/interrupt.h>

#include "twi_master.h"

#include <stdint.h>
#include <libintech/interrupt_manager.hpp>
#include "robot.h"
#define IGNORE_TWI_vect
#define IGNORE_TIMER1_OVF_vect
#include <libintech/isr.hpp>

INITIALISE_INTERRUPT_MANAGER();


int main()
{
	sei();
    Robot & robot = Robot::Instance();
	while(1)
	{
 		robot.communiquer_pc();
	}
	return 0;
}

ISR(TIMER1_OVF_vect, ISR_NOBLOCK)
{
	Robot & robot = Robot::Instance();
	
	//mise à jour des attribut stockant la distance parcourue en tic et l'angle courant en tic
	int32_t infos[2];
	get_all(infos);
    
	robot.mesure_distance(infos[0] + infos[1]);
	robot.mesure_angle(infos[0] - infos[1]);
	
	//mise à jour du pwm envoyé aux moteurs pour l'asservissement
	robot.asservir();
	
	//calcul de la nouvelle position courante du robot, en absolu sur la table (mm et radians)
	robot.update_position();
}
