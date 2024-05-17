import { Component, OnInit } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { AuthService } from '../service/auth.service';
import { UserService } from '../services/user.service';
import { User } from '../model/user.model';

@Component({
  selector: 'app-client-homepage',
  templateUrl: './client-homepage.component.html',
  styleUrls: ['./client-homepage.component.css']
})
export class ClientHomepageComponent implements OnInit{
  constructor(private http: HttpClient, private route : ActivatedRoute, private auth: AuthService, private userService: UserService) { }

  email: string | null = null;
  user!: User;
  private access_token = null;

  ngOnInit(): void {
    const emailParam = this.route.snapshot.paramMap.get('email');
    if (emailParam !== null && emailParam !== '') {
      this.email = emailParam;
      this.sendRequest();
    }
  }

  sendRequest() {
    this.http.get<any>('https://localhost:443/api/login/tokens/' + this.email, { observe: 'response' }).subscribe(
        response => this.processResponse(response),
        error => console.error(error)
    );
  }

  processResponse(response: any) {
    const accessToken = response.headers.get('Authorization');
    const refreshToken = response.headers.get('Refresh-Token');
    this.access_token = response.headers.get('Authorization');
    console.log("Odgovor:");
    console.log(response);
    console.log("TOKENI");
    console.log("Access token: " + accessToken);
    console.log("Refresh token: " + refreshToken);
    localStorage.setItem("jwt", accessToken || '');
    localStorage.setItem("refreshToken", refreshToken || '');
    if(accessToken !== null && refreshToken !== null)
      {
        this.getLoggedInUser();
      }
  }

  getLoggedInUser(): void{
    if(this.access_token)
    this.userService.getLoggedInUserHomepage(this.access_token).subscribe(
      (user: User) => {
        console.log("Uspesno dobavio ulogovanog usera: ", user);
        this.user = user;
        localStorage.setItem('loggedUserRole', this.user.roles.join(','));
      },
      (error) => {
        console.error('Error dobavljanja ulogovanog usera:', error);
      }
    );
  }
  }


