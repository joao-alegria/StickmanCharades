import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-scrollup',
  templateUrl: './scrollup.component.html',
  styleUrls: ['./scrollup.component.css']
})
export class ScrollupComponent implements OnInit {

  constructor() { }

  ngOnInit(): void {
  }

  toTop() {
    document.documentElement.scrollTop = 0;
  }
  
}
