/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.ua.deti.es.g54.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import pt.ua.deti.es.g54.entities.DBSession;
import pt.ua.deti.es.g54.entities.DBUser;

/**
 *
 * @author joaoalegria
 */
public class OngoingGame {
    private String sessionTopic;
    private DBSession session;
    private String currentWord;
    private Map<String, Integer> scores;
    private List<String> playersOrder;

    public OngoingGame(String sessionTopic, DBSession session) {
        this.sessionTopic = sessionTopic;
        this.session = session;
        this.scores=new HashMap();
        this.playersOrder=new ArrayList();
        for(DBUser user : session.getPlayers()){
            scores.put(user.getUsername(), 0);
            playersOrder.add(user.getUsername());
        }
        this.getNewWord();
    }
    
    public String getSessionTopic(){
        return this.sessionTopic;
    }
    
    public String getWord(){
        return this.currentWord;
    }
    
    public String getGuesser(){
        return this.playersOrder.get(0);
    }
    
    private void getNewWord(){
        this.currentWord=session.getRandomWord();
    }
    
    public boolean continueGame(){
        return this.playersOrder.size()>0;
    }
    
    public boolean assertGuess(String username, String guess){
        if(currentWord.equals(guess)){
            playersOrder.remove(0);
            scores.put(username, scores.get(username)+1);
            this.getNewWord();
            return true;
        }
        return false;
    }
    
    public String getWinner(){
        String winner="";
        int winnerScore=0;
        for(String user : scores.keySet()){
            if(scores.get(user)>winnerScore){
                winner=user;
            }
        }
        return winner;
    }
}
