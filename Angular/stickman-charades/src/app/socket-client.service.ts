import { Injectable } from '@angular/core';
import { SocketClientState } from './SocketClientState';
import { Observable } from 'rxjs';
import * as Stomp from '../assets/js/stomp.min.js';
import * as SockJS from '../assets/js/sockjs.min.js';


@Injectable({
  providedIn: 'root'
})
export class SocketClientService {

  private client: Stomp.Client;
  private state: Stomp.BehaviorSubject<SocketClientState>;

  constructor() { 
    this.client = Stomp.over(new SockJS("http://192.168.160.103:54880"));
    this.state = new Stomp.BehaviorSubject<SocketClientState>(SocketClientState.ATTEMPTING);
    this.client.connect({}, () => {
      this.state.next(SocketClientState.CONNECTED);
    });
  }

  private connect(): Observable<Stomp.Client> {
    return new Observable<Stomp.Client>(observer => {
      this.state.pipe(Stomp.filter(state => state === SocketClientState.CONNECTED)).subscribe(() => {
        observer.next(this.client);
      });
    });
  }

  onMessage(topic: string, handler = SocketClientService.jsonHandler): Observable<any> {
    return this.connect().pipe(Stomp.first(), Stomp.switchMap(client => {
      return new Observable<any>(observer => {
        const subscription: Stomp.StompSubscription = client.subscribe(topic, message => {
          observer.next(handler(message.body));
        });
        return () => client.unsubscribe(subscription.id);
      });
    }));
  }

  static jsonHandler(message: Stomp.Message): any {
    return JSON.parse(message.body);
  }
  
  static textHandler(message: Stomp.Message): string {
    return message.body;
  }

}
