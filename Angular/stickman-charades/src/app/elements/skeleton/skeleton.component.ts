import { Component, OnInit, Input, SimpleChanges } from '@angular/core';
import * as d3 from 'd3';
import * as $ from "jquery";

// import { SocketClientService } from '../../socket-client.service';
import * as SockJS from '../../../assets/js/sockjs.min.js';
import * as Stomp from '../../../assets/js/stomp.min.js';
// import { over } from '../../../assets/js/sockjs.min.js';


// was on master vv
//import { Observable } from 'rxjs';
//import * as Stomp from '../../../assets/js/stomp.min.js';
//import * as SockJS from '../../../assets/js/sockjs.min.js';
//import { SocketClientService } from '../../socket-client.service';
// was on master ^^

@Component({
  selector: 'app-skeleton',
  templateUrl: './skeleton.component.html',
  styleUrls: ['./skeleton.component.css']
})
export class SkeletonComponent implements OnInit {
  client: any
  newMessage: string;
  skeleton: any

  mapping = ["Head", "Neck", "Torso", "Waist", "RightHip", "LeftHip", "RightKnee", "LeftKnee", "RightAnkle", "LeftAnkle", "RightHand", "RightWrist", "RightElbow", "RightShoulder", "RightCollar", "LeftCollar", "LeftShoulder", "LeftElbow", "LeftWrist", "LeftHand"]

  constructor() { }//private socketClient: SocketClientService) { }

  @Input()
  public set session(session: number) {
    this.clear()
    this.clearSocket()
    this.newSocket(session)
    console.log(session);
  }

  ngOnInit(): void {
    // Variables
    var h = 200;  // The height of the visualization
    var w = 400;  // The width of the visualization

    var parent = d3.select("body").select("#container");

    let svg = parent.append("svg")
      .attr("width", w)
      .attr("height", h)
      .attr("overflow", "scroll")
      .style("display", "inline-block");


    // Read the files
    $.getJSON("../../assets/skeleton.json", (json1) => {
      this.skeleton = json1;
    });

  }

  draw(positions) {
    var svg = d3.select("body").select("#container").select("svg");

    let headJoint = 0;

    svg.selectAll("circle.joints")
      .data(positions)
      .enter()
      .append("circle")
      .attr("cx", function (d) {
        return d.x;
      }).attr("cy", function (d) {
        return d.y;
      }).attr("r", function (d, i) {
        if (i == headJoint)
          return 4;
        else
          return 2;
      }).attr("fill", function (d, i) {
        return '#555555';
      });

    // Bones
    svg.selectAll("line.bones")
      .data(this.skeleton)
      .enter()
      .append("line")
      .attr("stroke", "#555555")
      .attr("stroke-width", 1)
      .attr("x1", 0).attr("x2", 0)
      .attr("x1", function (d, j) {
        return positions[d[0]].x;
      })
      .attr("x2", function (d, j) {
        return positions[d[1]].x;
      })
      .attr("y1", function (d, j) {
        return positions[d[0]].y;
      })
      .attr("y2", function (d, j) {
        return positions[d[1]].y;
      });
  }

  clear() {
    var svg = d3.select("body").select("#container").select("svg")
    svg.selectAll("*").remove()
  }

  clearSocket() {
    let _this = this
    if (_this.client != null) {
      _this.client.disconnect();
    }
  }

  newSocket(session) {
    var wsocket = new SockJS('http://localhost:8084/game/skeletons', null, { headers: { 'Authorization': 'Basic ' + localStorage.getItem("TOKEN") } });
    this.client = Stomp.Stomp.over(wsocket);
    let _this = this
    _this.client.connect({}, function (frame) {
      _this.client.subscribe('/game/esp54_' + session, message => {
        console.info(message)
        this.clear()
        // let data = { "index": 0, "positions": { "Head": [2098.635, 1708.936, 0.0], "Neck": [2227.439, 1410.903, 0.0], "LeftCollar": [2252.219, 1217.666, 0.0], "Torso": [2314.787, 729.7457, 0.0], "Waist": [2356.344, 278.0999, 0.0], "LeftShoulder": [1860.994, 1193.861, 0.0], "RightShoulder": [2813.465, 1175.847, 0.0], "LeftElbow": [1751.346, 713.7789, 0.0], "RightElbow": [0.0, 0.0, 0.0], "LeftWrist": [0.0, 0.0, 0.0], "RightWrist": [0.0, 0.0, 0.0], "LeftHand": [0.0, 1800.0, 0.0], "RightHand": [0.0, 0.0, 0.0], "LeftHip": [0.0, 0.0, 0.0], "RightHip": [2672.045, 226.5393, 0.0], "LeftKnee": [0.0, 0.0, 0.0], "RightKnee": [0.0, 0.0, 0.0], "LeftAnkle": [0.0, 0.0, 0.0], "RightAnkle": [0.0, 0.0, 0.0] } }
        let data = JSON.parse(message);
        let positions = [];
        for (let tmp of this.mapping) {
          if (tmp in data["positions"]) {
            positions.push({ x: data["positions"][tmp][0] / 10, y: data["positions"][tmp][1] / 10, z: data["positions"][tmp][2] / 10 })
          } else {
            positions.push(positions[positions.length - 1])//??????
          }
        }
        this.draw(positions)
      });
    });
  }

}
