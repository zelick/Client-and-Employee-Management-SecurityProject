import { Component } from '@angular/core';
import { AdRequest } from '../model/adRequest.model';
import { UserService } from '../services/user.service';

@Component({
  selector: 'app-ad-request-form',
  templateUrl: './ad-request-form.component.html',
  styleUrls: ['./ad-request-form.component.css']
})
export class AdRequestFormComponent {
  adRequest: AdRequest = {
    email: '',
    deadline: new Date(),
    activeFrom: new Date(),
    activeTo: new Date(),
    description: ''
  };

  constructor(private userService: UserService) {}

  onSubmit() {
    this.userService.createAdRequest(this.adRequest).subscribe(
      (response) => {
        console.log('Ad request created successfully');
        // Dodajte ovde logiku za obradu uspešnog odgovora ako je potrebno
      },
      (error) => {
        console.error('Error creating ad request:', error);
        // Dodajte ovde logiku za obradu greške ako je potrebno
      }
    );
  }
}
