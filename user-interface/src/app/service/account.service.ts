import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Observable} from "rxjs";
import {AuthenticationService} from "./authentication.service";
import {Account} from "../domain/Account";

@Injectable({
    providedIn: 'root'
})
export class AccountService {
    private currentAccountUrl = 'api/accounts/current';

    constructor(private http: HttpClient) {
    }

    public getCurrentAccount(): Observable<Account> {
        let account = AccountService.getCurrentAccountFromSession();

        if (account == null) {
            return this.requestServer();
        }

        return new Observable(obs => {
            obs.next(account);
        })
    }


    public saveAccount(account: Account): Observable<void> {
        let token = AuthenticationService.getOauthToken();

        let headers = new HttpHeaders({'Authorization': 'Bearer ' + token});
        let options = {
            headers: headers
        };

        let request = this.http.put<void>(this.currentAccountUrl, account, options);

        request.subscribe(() => {
            AccountService.saveAccount(account);
        });

        return request;
    }

    private static getCurrentAccountFromSession(): Account {
        return JSON.parse(localStorage.getItem('account'))
    }


    private requestServer(): Observable<Account> {
        var observable = this.createRequest();
        observable.subscribe(result => AccountService.saveAccount(result));
        return observable;
    }

    private createRequest(): Observable<Account> {
        let token = AuthenticationService.getOauthToken();

        let headers = new HttpHeaders({'Authorization': 'Bearer ' + token});
        let options = {
            headers: headers
        };

        return this.http.get<Account>(this.currentAccountUrl, options);
    }

    private static saveAccount(account: Account) {
        localStorage.setItem('account', JSON.stringify(account));
    }

}
