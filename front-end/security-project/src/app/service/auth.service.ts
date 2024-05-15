import { HttpHeaders } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { ApiService } from "./api.service";
import { Router } from "@angular/router";
import { ConfigService } from "./config.service";
import {map} from 'rxjs/operators';
import { UserService } from "../services/user.service";
import { ResponseMessage } from "../model/responseMessage.model";
import { User } from "../model/user.model";


@Injectable()
export class AuthService {

  constructor(
    private apiService: ApiService,
    private userService: UserService,
    private config: ConfigService,
    private router: Router
  ){
  }

  user: User | undefined;

  private access_token = null;

  login(user:any) {
    const loginHeaders = new HttpHeaders({
      'Accept': 'application/json',
      'Content-Type': 'application/json'
    });
    // const body = `username=${user.username}&password=${user.password}`;
    const body = {
      'username': user.username,
      'password': user.password
    };
    console.log("username:", user.username);
    console.log("password:", user.password);
    console.log("putanja:",this.config.login_url );

    return this.apiService.post(this.config.login_url, JSON.stringify(body), loginHeaders)
    .pipe(map((res) => {
      console.log('Login success');
      if (res.body && res.body.accessToken) {
        this.access_token = res.body.accessToken;
        localStorage.setItem("jwt", res.body.accessToken);
        console.log(this.access_token);
        this.getLoggedInUser();
      } else {
        console.error('Invalid response or missing access token:', res);
      }
    }));
  }

  getLoggedInUser() {
    this.userService.getLoggedInUser().subscribe(
      (user: User) => {
        console.log("Uspesno dobavio ulogovanog usera: ", user);
        this.user = user;
        console.log('ROLA ULOGOVANOG KORISNIKA: ' + this.user.role);
        localStorage.setItem('loggedUserRole', this.user.role);
      },
      (error) => {
        console.error('Error dobavljanja ulogovanog usera:', error);
      }
    );
  }

  getLoggedInUserRole() {
    if (localStorage.getItem('loggedUserRole') == null) {
      return "UNAUTHORIZE";
    }
    return localStorage.getItem('loggedUserRole');
  }

  signup(user:any) { //NE TREBA OVO 
    const signupHeaders = new HttpHeaders({
      'Accept': 'application/json',
      'Content-Type': 'application/json'
    });
    return this.apiService.post(this.config.signup_url, JSON.stringify(user), signupHeaders)
      .pipe(map(() => {
        console.log('Sign up success');
      }));
  }

  logout() {
    //this.userService.currentUser = null;
    localStorage.removeItem("loggedUserRole")
    localStorage.removeItem("jwt");
    this.access_token = null;
    console.log("KAD SE ODJAVI: " + localStorage.getItem("loggedUserRole"));
    this.router.navigate(['/']);
  }

  tokenIsPresent() {
    return this.access_token != undefined && this.access_token != null;
  }

  getToken() {
    return this.access_token;
  }

  isAuthenticated(): boolean {
    const token = localStorage.getItem('jwt');
    return !!token; 
  }

}