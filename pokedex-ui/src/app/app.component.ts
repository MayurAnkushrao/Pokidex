import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { forkJoin } from 'rxjs';
import { PokemonDto, PokemonService } from './pokemon.service';

type SortOrder = 'id-asc' | 'id-desc' | 'name-asc';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css',
})
export class AppComponent implements OnInit {
  title = 'Pokédex';

  searchName = '';
  pokemonList: PokemonDto[] = [];
  listLoading = false;
  error: string | null = null;

  selected: PokemonDto | null = null;

  // Initial grid (first row like the official site)
  initialNames = [
    'bulbasaur',
    'ivysaur',
    'venusaur',
    'charmander',
    'charmeleon',
    'charizard',
    'squirtle',
    'wartortle',
    'blastoise',
  ];

  sortOrder: SortOrder = 'id-asc';

  // PokeAPI currently has 1025+ Pokémon and supports lookup by numeric id or name. [attached_file:1]
  readonly maxPokemonId = 1025;

  constructor(private pokemonService: PokemonService) {}

  ngOnInit(): void {
    this.loadInitialGrid();
  }

  private loadInitialGrid(): void {
    this.listLoading = true;
    this.error = null;

    forkJoin(
      this.initialNames.map((n) => this.pokemonService.getPokemonByName(n))
    ).subscribe({
      next: (list) => {
        this.pokemonList = list;
        this.applySort();
        this.listLoading = false;
      },
      error: () => {
        this.error = 'Failed to load Pokédex list.';
        this.listLoading = false;
      },
    });
  }

  applySort(): void {
    const list = [...this.pokemonList];

    if (this.sortOrder === 'id-asc') {
      list.sort((a, b) => a.id - b.id);
    } else if (this.sortOrder === 'id-desc') {
      list.sort((a, b) => b.id - a.id);
    } else {
      list.sort((a, b) => a.name.localeCompare(b.name));
    }

    this.pokemonList = list;
  }

  // When clicking a card in the grid
  onCardClick(p: PokemonDto): void {
    this.selected = p;
    this.searchName = p.name;
    this.scrollToDetail();
  }

  // Search box behaviour
  onSearch(): void {
    const name = this.searchName.trim().toLowerCase();
    if (!name) {
      this.error = 'Please enter a Pokémon name.';
      this.selected = null;
      return;
    }

    this.error = null;

    this.pokemonService.getPokemonByName(name).subscribe({
      next: (p) => {
        this.selected = p;
        if (!this.pokemonList.find((x) => x.id === p.id)) {
          this.pokemonList = [...this.pokemonList, p];
          this.applySort();
        }
        this.scrollToDetail();
      },
      error: (err) => {
        this.selected = null;
        if (err.status === 404) {
          this.error = 'Pokémon not found.';
        } else {
          this.error = 'Failed to fetch Pokémon details.';
        }
      },
    });
  }

  // Surprise Me: pick a random numeric id and load that Pokémon
  surpriseMe(): void {
    const randomId = Math.floor(Math.random() * this.maxPokemonId) + 1;
    this.error = null;

    this.pokemonService.getPokemonByName(String(randomId)).subscribe({
      next: (p) => {
        this.selected = p;
        this.searchName = p.name;
        if (!this.pokemonList.find((x) => x.id === p.id)) {
          this.pokemonList = [...this.pokemonList, p];
          this.applySort();
        }
        this.scrollToDetail();
      },
      error: () => {
        this.error = 'Failed to fetch random Pokémon.';
      },
    });
  }

  private scrollToDetail(): void {
    setTimeout(() => {
      document
        .querySelector('.detail-section')
        ?.scrollIntoView({ behavior: 'smooth', block: 'start' });
    }, 0);
  }

  // Helper to iterate stats in template
  get statEntries() {
    if (!this.selected) return [];
    return Object.entries(this.selected.stats);
  }
}
