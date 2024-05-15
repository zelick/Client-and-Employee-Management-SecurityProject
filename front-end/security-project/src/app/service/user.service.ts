import { Injectable } from "@angular/core";
import { ApiService } from "./api.service";
import { ConfigService } from "./config.service";
import { map } from "rxjs/operators";
import { Observable } from "rxjs";
import { HttpClient } from "@angular/common/http";


@Injectable({
    providedIn: 'root'
  })
  export class UserService {
  
    currentUser!:any;
  
    constructor(
      private apiService: ApiService,private config: ConfigService, private http: HttpClient) {}
  
    // getMyInfo() {
    //   return this.apiService.get(this.config.whoami_url)
    //     .pipe(map(user => {
    //       this.currentUser = user;
    //       return user;
    //     }));
    // }
  
    getAll() {
      return this.apiService.get(this.config.users_url);
    }


    //OVO JE PROBA SAMO OBRISI
    getUserByEmail(email: string): Observable<any> {
      const url = `http://localhost:8080/api/auth/getUserByEmail/${email}`;
      console.log("URL za dobijanje korisnika po emailu:", url);
      return this.http.get<any>(url);
    }
    
    
  
  }