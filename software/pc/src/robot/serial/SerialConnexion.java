package robot.serial;

import enums.ServiceNames;
import exceptions.serial.SerialConnexionException;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;

import java.io.*;
import java.nio.file.Files;

import utils.Log;
import container.Service;

/**
 * Classe implÃƒÂ©mentant le concept d'une connexion sÃƒÂ©rie.
 * UtilisÃƒÂ©e pour parler aux cartes ÃƒÂ©lectroniques.
 * Chaque port a un nom (asserv par exemple), un id (0 par exemple), un port (/dev/ttyUSB0 par exemple)
 * et un baudrate (57600 par exemple, c'est la vitesse de communication).
 * @author karton, dede, kayou, pf
 *
 */
public class SerialConnexion implements SerialPortEventListener, Service
{
    /**
     * Port de la connexion
     */
    SerialPort serialPort;

    /**
     * Sortie de log a utiliser
     */
    Log log;

    /**
     * Nom de la connexion sÃƒÂ©rie
     */
    String name;

    /**
     * Flux d'entÃ¯Â¿Â½e du port
     */
    private BufferedReader input;

    /**
     * Flux de sortie du port
     */
    private OutputStream output;

    /**
     * TIME_OUT d'attente de rÃ¯Â¿Â½ception d'un message
     */
    private static final int TIME_OUT = 2000;


    private BufferedWriter out;
    private boolean debug = true;

    /**
     * Construit une connexion sÃ¯Â¿Â½rie
     * @param log Sortie de log a utiliser
     * @param name nom de la connexion sÃƒÂ©rie
     */
    SerialConnexion (Log log, ServiceNames name)
    {
        this(log, name.toString());
    }

    /**
     * Construit une connexion sÃ¯Â¿Â½rie
     * @param log Sortie de log a utiliser
     * @param name nom de la connexion sÃƒÂ©rie
     */
    public SerialConnexion (Log log, String name)
    {
        super();
        this.log = log;
        this.name = name;
        if(this.debug)
        {
            try
            {
                File file = new File("orders.txt");
                if (!file.exists())
                {
                    //file.delete();
                    file.createNewFile();
                }
                out = new BufferedWriter(new FileWriter(file));

            } catch (IOException e) {
                log.critical("Manque de droits pour l'output des ordres");
                //out = null;
                e.printStackTrace();
            }
        }
        else
            this.out = null;
    }

    /**
     * AppelÃ¯Â¿Â½ par le SerialManager, il donne Ã¯Â¿Â½ la sÃ¯Â¿Â½rie tout ce qu'il faut pour fonctionner
     * @param port_name : Le port oÃ¯Â¿Â½ est connectÃ¯Â¿Â½ la carte (/dev/ttyUSB ou /dev/ttyACM)
     * @param baudrate : Le baudrate que la carte utilise
     */
    public void initialize(String port_name, int baudrate)
    {
        CommPortIdentifier portId = null;
        try
        {
            portId = CommPortIdentifier.getPortIdentifier(port_name);
        }
        catch (NoSuchPortException e2)
        {
            log.critical("Catch de "+e2+" dans initialize");
        }

        // Ouverture du port sÃ¯Â¿Â½rie
        try
        {
            serialPort = (SerialPort) portId.open(this.getClass().getName(), TIME_OUT);
        }
        catch (PortInUseException e1)
        {
            log.critical("Catch de "+e1+" dans initialize");
        }
        try
        {
            // rÃ¯Â¿Â½gle certains paramÃ¯Â¿Â½tres liÃ¯Â¿Â½ Ã¯Â¿Â½ la sÃ¯Â¿Â½rie
            serialPort.setSerialPortParams(baudrate,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);

            // ouverture des flux Input/Output
            input = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
            output = serialPort.getOutputStream();

        }
        catch (Exception e)
        {
            log.critical("Catch de "+e+" dans initialize");
        }

        // permet d'avoir un readLine non bloquant
        try
        {
            serialPort.enableReceiveTimeout(1000);
        }
        catch (UnsupportedCommOperationException e)
        {
            log.critical("Catch de "+e+" dans initialize");
        }
    }

    /**
     * MÃ¯Â¿Â½thode pour communiquer Ã¯Â¿Â½ la liaison sÃ¯Â¿Â½rie. Il ne faut absolument pas se tromper sur le nombre de lignes attendu en retour.
     * (une ligne est dÃ¯Â¿Â½limitÃ¯Â¿Â½ par un "\r\n" sur une communication sÃ¯Â¿Â½rie. elle peut Ã¯Â¿Â½tre envoyÃ¯Â¿Â½ par le bas niveau dans un:
     * printf("\r\n") ou un printfln("...") oÃ¯Â¿Â½ ici le ln veut dire retour Ã¯Â¿Â½ la ligne donc se charge de mettre "\r\n" Ã¯Â¿Â½ la fin du message pour l'utilisateur).
     * @param message Message ÃƒÂ  envoyer
     * @param nb_lignes_reponse Nombre de lignes que le bas niveau va rÃƒÂ©pondre (sans compter les acquittements)
     * @return Un tableau contenant le message
     * @throws SerialConnexionException
     */
    public String[] communiquer(String message, int nb_lignes_reponse) throws SerialConnexionException
    {
        String[] messages = {message};
        return communiquer(messages, nb_lignes_reponse);
    }

    /**
     * MÃ¯Â¿Â½thode pour communiquer Ã¯Â¿Â½ la liaison sÃ¯Â¿Â½rie. Il ne faut absolument pas se tromper sur le nombre de lignes attendu en retour.
     * (une ligne est dÃ¯Â¿Â½limitÃ¯Â¿Â½ par un "\r\n" sur une communication sÃ¯Â¿Â½rie. elle peut Ã¯Â¿Â½tre envoyÃ¯Â¿Â½ par le bas niveau dans un:
     * printf("\r\n") ou un printfln("...") oÃ¯Â¿Â½ ici le ln veut dire retour Ã¯Â¿Â½ la ligne donc se charge de mettre "\r\n" Ã¯Â¿Â½ la fin du message pour l'utilisateur).
     * @param messages Messages ÃƒÂ  envoyer
     * @param nb_lignes_reponse Nombre de lignes que l'avr va rÃƒÂ©pondre (sans compter les acquittements)
     * @return Un tableau contenant le message
     * @throws SerialConnexionException
     */
    public String[] communiquer(String[] messages, int nb_lignes_reponse) throws SerialConnexionException
    {
        synchronized(output)
        {
            String inputLines[] = new String[nb_lignes_reponse];
            try
            {
                for (String m : messages)
                {
                    // affiche dans la console ce qu'on envois sur la sÃƒÂ©rie -> On cache ca, pour eviter le xy0? en permanence, mais ca peux etre interessant de le garder.
                    // ne jamais push un code avec cette ligne decommentee
					//log.debug("Envoi serie : '" + m  + "'");
                    m += "\r";

                    output.write(m.getBytes());
                    if(this.debug) 
                    {
                        out.write(m);
                        out.newLine();
                        out.flush();
                    }
                    int nb_tests = 0;
                    char acquittement = ' ';

                    while (acquittement != '_')
                    {
                        nb_tests++;

                        // affiche dans la console ce qu'on lit sur la sÃƒÂ©rie
                        String resposeFromCard = input.readLine();
                        //TODO commenter.
						//log.debug("Reception acquitement : '" + resposeFromCard  + "'");

                        acquittement = resposeFromCard.charAt(0);
                        if (acquittement != '_')
                        {
                            clearInputBuffer();
                            output.write(m.getBytes());
                        }
                        if (nb_tests > 10)
                        {
                            log.critical("La série " + this.name + " ne répond pas après " + nb_tests + " tentatives (envoyé : '" + m + "', reponse : '" + resposeFromCard + "')");
                            break;
                        }
                    }
                }
            }
            catch (Exception e)
            {
                log.critical("Ne peut pas parler a la carte " + this.name + " lancement de "+e);
                clearInputBuffer();
                communiquer(messages, nb_lignes_reponse);
            }

            try
            {
                for (int i = 0 ; i < nb_lignes_reponse; i++)
                {
                    inputLines[i] = input.readLine();

                    //TODO commenter.
					//log.debug("Ligne "+i+": '"+inputLines[i]+"'");
                    if(inputLines[i].equals(null) || inputLines[i].replaceAll(" ", "").equals("")|| inputLines[i].replaceAll(" ", "").equals("-"))
                    {
                        log.critical("='( , envoi de "+inputLines[i]+" envoi du message a nouveau");
                        clearInputBuffer();
                        communiquer(messages, nb_lignes_reponse);
                    }

                    if(!isAsciiExtended(inputLines[i]))
                    {
                        log.critical("='( , envoi de "+inputLines[i]+" envoi du message a nouveau");
                        clearInputBuffer();
                        communiquer(messages, nb_lignes_reponse); // On retente
                    }
                }
            }
            catch (Exception e)
            {
                log.critical("Ne peut pas parler a la carte " + this.name + " lancement de "+e);
                clearInputBuffer();
                communiquer(messages, nb_lignes_reponse);
            }
            return inputLines;
        }
    }

    /**
     * Doit ÃƒÂªtre appelÃƒÂ© quand on arrÃƒÂªte de se servir de la sÃƒÂ©rie
     */
    public void close()
    {
        if (serialPort != null)
        {
            log.debug("Fermeture de "+name);
            serialPort.close();
        }
    }

    /**
     * Handle an event on the serial port.
     * NE PAS SUPPRIMER!!!!!! Cette mÃ¯Â¿Â½thode est essentielle au fonctionnement de la communication sÃ¯Â¿Â½rie, mÃ¯Â¿Â½me si elle est vide.
     */
    public synchronized void serialEvent(SerialPortEvent oEvent)
    {
    }

    /**
     * Envoie un String sans chercher d'acquittement ou quoi que ce soit
     * SEULEMENT UTILE POUR LES YEUX
     * @param message le message
     */
    public synchronized void sendRaw(byte[] message) throws IOException {
        output.write(message);

    }

    /**
     * Ping de la carte.
     * Peut envoyer un message d'erreur lors de l'exÃ¯Â¿Â½cution de createSerial() dans SerialManager.
     *
     * (Avec la carte de test dans createSerial(), on ne sait pas encore si celle-ci va rÃ¯Â¿Â½pondre ou non, c'est Ã¯Â¿Â½ dire,
     * si il s'agit bien d'une liaison sÃ¯Â¿Â½rie, ou alors d'un autre pÃ¯Â¿Â½riphÃ¯Â¿Â½rique. Si il s'agit d'un autre pÃ¯Â¿Â½riphÃ¯Â¿Â½rique,
     * alors cette mÃ¯Â¿Â½thode va catch une exception)
     * UtilisÃƒÂ© que par createSerial de SerialManager
     * @return l'id de la carte
     */
    public synchronized String ping()
    {
        synchronized(output) {
            String ping = null;
            try
            {

                //Evacuation de l'eventuel buffer indÃƒÂ©sirable
                output.write("CeciNestPasUnOrdre\r".getBytes());
                //evacuation de l'acquittement "_"
                input.readLine();
                //evacuation de reponse "Ordre inonnu"
                input.readLine();

                //ping
                output.write("?\r".getBytes());
                //evacuation de l'acquittement
                input.readLine();

                //recuperation de l'id de la carte
                ping = input.readLine();

            }
            catch (Exception e)
            {
                log.critical("Catch de "+e+" dans ping");
            }
            return ping;
        }
    }


    public void updateConfig()
    {
    }


    /**
     * Fonction verifiant si on recoit bien de l'ascii etendu : sinon, bah le bas niveau deconne.
     * @param inputLines
     * @return
     * @throws Exception
     */
    @SuppressWarnings("javadoc")
    public boolean isAsciiExtended(String inputLines) throws Exception
    {
        for (int i = 0; i < inputLines.length(); i++)
        {
            if (inputLines.charAt(i) > 259)
            {
                log.critical(inputLines+" n'est pas ASCII");
                return false;
            }
        }
        return true;
    }

    public synchronized void clearInputBuffer()
    {
        log.debug("SerialConnexion : Tentative de clean du buffer d'input");
        try
        {
            while(input.read() != -1);
        }
        catch (IOException e)
        {
            log.debug("Reconstruction de la série");
            try {
                input = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }
}