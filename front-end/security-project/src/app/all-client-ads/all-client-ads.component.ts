import { Component, OnInit } from '@angular/core';
import { Ad } from '../model/ad.model';
import { UserService } from '../services/user.service';

@Component({
  selector: 'app-all-client-ads',
  templateUrl: './all-client-ads.component.html',
  styleUrls: ['./all-client-ads.component.css']
})
export class AllClientAdsComponent {
  allAds: Ad[] = [];

  constructor(private userService: UserService) { }

  ngOnInit(): void {
    this.userService.getAllAdsByEmail('pmilica990@gmail.com').subscribe(
      (data: Ad[]) => {
        this.allAds = data;
      },
      (error) => {
        console.error('Error fetching ad requests: ', error);
      }
    );
  }

}
