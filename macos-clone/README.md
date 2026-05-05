# NovaOS React (inspirado no macOS)

Desktop web autoral inspirado em sistemas modernos, agora em React + Vite.

## Recursos

- Tema claro/escuro com alternancia na barra superior
- Persistencia em localStorage de:
  - tema atual
  - posicao/tamanho/estado de cada janela
- Componentes reutilizaveis para menu, dock, icones e janelas

## Como executar

1. Entre na pasta do projeto:

	`cd macos-clone`

2. Instale as dependencias:

	`npm install`

3. Rode em modo desenvolvimento:

	`npm run dev`

4. Build de producao:

	`npm run build`

## Estrutura

- `src/App.jsx`: estado global (tema e janelas)
- `src/components/WindowFrame.jsx`: janela reutilizavel
- `src/components/MenuBar.jsx`: barra superior + toggle de tema
- `src/components/Dock.jsx`: dock com acoes
- `src/components/DesktopIcons.jsx`: icones da area de trabalho
