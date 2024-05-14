import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { AdRequest } from '../model/adRequest.model';
import { UserService } from '../services/user.service';
import { User } from '../model/user.model';
import { Ad } from '../model/ad.model';

@Component({
  selector: 'app-ad-form',
  templateUrl: './ad-form.component.html',
  styleUrls: ['./ad-form.component.css']
})
export class AdFormComponent implements OnInit {
  adRequest!: AdRequest;
  client!: User;
  description: string = '';
  slogan: string = '';

  constructor(private route: ActivatedRoute, private userService: UserService) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const id = Number(params.get('id')); 
      if (!isNaN(id)) {
        this.userService.getAdRequestById(id).subscribe(adRequest => {
          this.adRequest = adRequest;
          console.log(adRequest);
          this.findUser();
        });
      }
    });
  }

  findUser(): void{
    this.userService.findUserByEmail().subscribe(user => {
      this.client = user;
      console.log(user);
    });
  }

  onSubmit(): void {
    const ad: Ad = { 
      email: this.client.email,
      name: this.client.name,
      surname: this.client.surname, 
      activeFrom: this.adRequest.activeFrom, 
      activeTo: this.adRequest.activeTo, 
      description: this.description, 
      slogan: this.slogan 
    };
    console.log(ad);

    this.userService.createAd(ad).subscribe(response => {
      console.log(ad);
      console.log(response);
    });
  }

}
