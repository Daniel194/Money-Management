import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {AuthenticationService} from "./authentication.service";
import {DataPoint} from "../domain/DataPoint";
import {Observable} from "rxjs";

@Injectable({
    providedIn: 'root'
})
export class StatisticsService {
    private currentAccountStatistics = 'api/statistics/current';
    private betweenAccountStatistics = 'api/statistics/between';

    constructor(private http: HttpClient) {
    }


    public getCurrentAccountStatistics(): Observable<DataPoint[]> {
        let token = AuthenticationService.getOauthToken();

        let headers = new HttpHeaders({'Authorization': 'Bearer ' + token});
        let options = {
            headers: headers
        };

        return this.http.get<DataPoint[]>(this.currentAccountStatistics, options);
    }

    public getAccountStatisticsBetweenDates(startDate: Date, endDate: Date): Observable<DataPoint[]> {
        let token = AuthenticationService.getOauthToken();

        let headers = new HttpHeaders({'Authorization': 'Bearer ' + token});
        let options = {
            headers: headers
        };

        let url = this.betweenAccountStatistics + "?";
        url += "beginDate=" + startDate.getFullYear() + "-" + (startDate.getMonth() + 1) + "-" + (startDate.getDay() + 1);
        url += "&endDate=" + endDate.getFullYear() + "-" + (endDate.getMonth() + 1) + "-" + (endDate.getDay() + 1);

        return this.http.get<DataPoint[]>(url, options);
    }

}
