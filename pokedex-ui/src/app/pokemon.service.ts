// src/app/pokemon.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface PokemonDto {
  name: string;
  id: number;
  height: number;
  weight: number;
  types: string[];
  spriteFront: string | null;
  artwork: string | null;
  stats: { [key: string]: number };
  abilities: string[];
  moves: string[];
  cached: boolean;
  fetchedAt: string;
}

@Injectable({
  providedIn: 'root',
})
export class PokemonService {
  private readonly baseUrl = 'http://localhost:8080/api/pokemon';

  constructor(private http: HttpClient) {}

  getPokemonByName(name: string): Observable<PokemonDto> {
    return this.http.get<PokemonDto>(`${this.baseUrl}/${name}`);
  }
}
