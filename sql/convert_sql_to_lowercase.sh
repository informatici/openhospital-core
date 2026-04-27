#!/bin/bash

# Script pour convertir tous les noms de tables, champs et requêtes SQL en minuscules
# Auteur: Assistant
# Date: $(date +%Y-%m-%d)

# Couleurs pour l'affichage
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Répertoire par défaut contenant les fichiers SQL
SQL_DIR="/home/uni2grow/Documents/oh/openhospital-core/sql"

# Fonction pour afficher l'aide
show_help() {
    echo -e "${BLUE}Usage: $0 [OPTIONS] [REPERTOIRE]${NC}"
    echo ""
    echo -e "${YELLOW}Options:${NC}"
    echo "  -h, --help     Afficher cette aide"
    echo "  -v, --verbose  Mode verbeux"
    echo "  -d, --dry-run  Simuler les modifications sans appliquer"
    echo ""
    echo -e "${YELLOW}Arguments:${NC}"
    echo "  REPERTOIRE     Répertoire contenant les fichiers SQL (défaut: $SQL_DIR)"
    echo ""
    echo -e "${YELLOW}Exemples:${NC}"
    echo "  $0                           # Utiliser le répertoire par défaut"
    echo "  $0 /path/to/sql/files       # Utiliser un répertoire spécifique"
    echo "  $0 -v --dry-run             # Mode verbeux et simulation"
    echo ""
    echo -e "${GREEN}Description:${NC}"
    echo "  Ce script convertit tous les noms de tables, champs et requêtes SQL"
    echo "  en minuscules dans tous les fichiers .sql du répertoire spécifié."
    echo "  Il traite récursivement tous les sous-répertoires."
}

# Fonction pour vérifier si Python 3 est disponible
check_python() {
    if ! command -v python3 &> /dev/null; then
        echo -e "${RED}Erreur: Python 3 n'est pas installé ou n'est pas dans le PATH${NC}"
        echo "Veuillez installer Python 3 pour utiliser ce script."
        exit 1
    fi
}

# Fonction pour créer le script Python temporaire
create_python_script() {
    cat > /tmp/convert_sql_to_lowercase.py << 'EOF'
#!/usr/bin/env python3
import os
import re
import sys

def convert_sql_to_lowercase(file_path, verbose=False):
    """Convert SQL file content to lowercase for tables, fields and keywords"""

    try:
        with open(file_path, 'r', encoding='utf-8') as f:
            content = f.read()
    except Exception as e:
        print(f"Erreur de lecture du fichier {file_path}: {e}")
        return False

    original_content = content

    # Convert SQL keywords to lowercase
    sql_keywords = [
        'CREATE', 'TABLE', 'INSERT', 'INTO', 'VALUES', 'UPDATE', 'SET',
        'DELETE', 'FROM', 'SELECT', 'WHERE', 'AND', 'OR', 'NOT', 'NULL',
        'PRIMARY', 'KEY', 'FOREIGN', 'REFERENCES', 'ON', 'DELETE', 'CASCADE',
        'UPDATE', 'NO', 'ACTION', 'DEFAULT', 'AUTO_INCREMENT', 'INT', 'VARCHAR',
        'TEXT', 'DATETIME', 'FLOAT', 'CHAR', 'ENGINE', 'INNODB', 'CHARACTER', 'SET',
        'UTF8', 'UNIQUE', 'INDEX', 'ALTER', 'ADD', 'COLUMN', 'CHANGE', 'DROP',
        'IF', 'EXISTS', 'CONSTRAINT', 'FOREIGN', 'KEY', 'REFERENCES', 'LOAD',
        'DATA', 'LOCAL', 'INFILE', 'FIELDS', 'TERMINATED', 'BY', 'LINES',
        'GROUP', 'BY', 'ORDER', 'HAVING', 'JOIN', 'LEFT', 'RIGHT', 'INNER',
        'OUTER', 'UNION', 'DISTINCT', 'COUNT', 'SUM', 'AVG', 'MAX', 'MIN',
        'DATE', 'SUBSTRING', 'LOCATE', 'DATE_SUB', 'INTERVAL', 'MONTH', 'YEAR',
        'CURDATE', 'TIMESTAMPDIFF', 'CASE', 'WHEN', 'THEN', 'ELSE', 'END',
        'DECLARE', 'CURSOR', 'FOR', 'DO', 'BEGIN', 'END', 'IF', 'LEAVE',
        'LOOP', 'CLOSE', 'FETCH', 'CONTINUE', 'HANDLER', 'NOT', 'FOUND'
    ]

    for keyword in sql_keywords:
        pattern = r'(?<![\w`"])' + re.escape(keyword) + r'(?![\w`"])'
        content = re.sub(pattern, keyword.lower(), content, flags=re.IGNORECASE)

    # Convert table names
    content = re.sub(r'(create\s+table\s+)([A-Z][A-Z0-9_]*)', lambda m: m.group(1) + m.group(2).lower(), content, flags=re.IGNORECASE)
    content = re.sub(r'(insert\s+into\s+)([A-Z][A-Z0-9_]*)', lambda m: m.group(1) + m.group(2).lower(), content, flags=re.IGNORECASE)
    content = re.sub(r'(update\s+)([A-Z][A-Z0-9_]*)', lambda m: m.group(1) + m.group(2).lower(), content, flags=re.IGNORECASE)
    content = re.sub(r'(delete\s+from\s+)([A-Z][A-Z0-9_]*)', lambda m: m.group(1) + m.group(2).lower(), content, flags=re.IGNORECASE)
    content = re.sub(r'(\s+from\s+)([A-Z][A-Z0-9_]*)(?!\s*\()', lambda m: m.group(1) + m.group(2).lower(), content, flags=re.IGNORECASE)
    content = re.sub(r'(\s+(?:left\s+|right\s+|inner\s+|outer\s+)?join\s+)([A-Z][A-Z0-9_]*)', lambda m: m.group(1) + m.group(2).lower(), content, flags=re.IGNORECASE)
    content = re.sub(r'(references\s+)([A-Z][A-Z0-9_]*)', lambda m: m.group(1) + m.group(2).lower(), content, flags=re.IGNORECASE)
    content = re.sub(r'(into\s+table\s+)([A-Z][A-Z0-9_]*)', lambda m: m.group(1) + m.group(2).lower(), content, flags=re.IGNORECASE)

    # Convert field names
    content = re.sub(r'(\s+)([A-Z][A-Z0-9_]*)\s+(int|varchar|text|datetime|float|char|date|time|timestamp|blob|longtext|mediumtext|tinytext|enum|set)', lambda m: m.group(1) + m.group(2).lower() + ' ' + m.group(3).lower(), content, flags=re.IGNORECASE)
    content = re.sub(r'(insert\s+into\s+[a-z0-9_]+\s*\()([A-Z][A-Z0-9_]*(?:\s*,\s*[A-Z][A-Z0-9_]*)*)', lambda m: m.group(1) + re.sub(r'([A-Z][A-Z0-9_]*)', lambda x: x.group(1).lower(), m.group(2)), content, flags=re.IGNORECASE)
    content = re.sub(r'(update\s+[a-z0-9_]+\s+set\s+)([A-Z][A-Z0-9_]*)', lambda m: m.group(1) + m.group(2).lower(), content, flags=re.IGNORECASE)
    content = re.sub(r'(\s+where\s+)([A-Z][A-Z0-9_]*)', lambda m: m.group(1) + m.group(2).lower(), content, flags=re.IGNORECASE)
    content = re.sub(r'(\s+order\s+by\s+)([A-Z][A-Z0-9_]*)', lambda m: m.group(1) + m.group(2).lower(), content, flags=re.IGNORECASE)
    content = re.sub(r'(\s+group\s+by\s+)([A-Z][A-Z0-9_]*)', lambda m: m.group(1) + m.group(2).lower(), content, flags=re.IGNORECASE)
    content = re.sub(r'(primary\s+key\s*\()([A-Z][A-Z0-9_]*)', lambda m: m.group(1) + m.group(2).lower(), content, flags=re.IGNORECASE)
    content = re.sub(r'(foreign\s+key\s*\()([A-Z][A-Z0-9_]*)', lambda m: m.group(1) + m.group(2).lower(), content, flags=re.IGNORECASE)
    content = re.sub(r'(select\s+)([A-Z][A-Z0-9_]*(?:\s*,\s*[A-Z][A-Z0-9_]*)*)', lambda m: m.group(1) + re.sub(r'([A-Z][A-Z0-9_]*)', lambda x: x.group(1).lower() if not x.group(1).isdigit() else x.group(1), m.group(2)), content, flags=re.IGNORECASE)

    # Fix remaining uppercase patterns
    content = re.sub(r'\(([A-Z][A-Z0-9_]*)\)', lambda m: '(' + m.group(1).lower() + ')', content)
    content = re.sub(r'\b([A-Z][A-Z0-9_]*[A-Z0-9])\b', lambda m: m.group(1).lower() if '_' in m.group(1) or len(m.group(1)) > 2 else m.group(1), content)

    return content != original_content, content

def process_sql_directory(directory, dry_run=False, verbose=False):
    """Process all SQL files in the given directory and subdirectories"""
    processed_files = []
    skipped_files = []
    error_files = []

    for root, dirs, files in os.walk(directory):
        for file in files:
            if file.lower().endswith('.sql'):
                file_path = os.path.join(root, file)
                try:
                    changed, content = convert_sql_to_lowercase(file_path, verbose)
                    if changed:
                        if dry_run:
                            processed_files.append(file_path)
                            if verbose:
                                print(f"[DRY RUN] Serait modifié: {file_path}")
                        else:
                            with open(file_path, 'w', encoding='utf-8') as f:
                                f.write(content)
                            processed_files.append(file_path)
                            if verbose:
                                print(f"Modifié: {file_path}")
                    else:
                        skipped_files.append(file_path)
                        if verbose:
                            print(f"Pas de modification nécessaire: {file_path}")
                except Exception as e:
                    error_files.append((file_path, str(e)))
                    print(f"Erreur lors du traitement de {file_path}: {e}")

    return processed_files, skipped_files, error_files

if __name__ == "__main__":
    import argparse

    parser = argparse.ArgumentParser(description='Convert SQL files to lowercase')
    parser.add_argument('directory', help='Directory containing SQL files')
    parser.add_argument('--dry-run', action='store_true', help='Simulate changes without applying')
    parser.add_argument('--verbose', action='store_true', help='Verbose output')

    args = parser.parse_args()

    processed, skipped, errors = process_sql_directory(args.directory, args.dry_run, args.verbose)

    print(f"\nRésumé:")
    print(f"Fichiers traités: {len(processed)}")
    print(f"Fichiers ignorés (pas de modifications): {len(skipped)}")
    print(f"Erreurs: {len(errors)}")

    if errors:
        print(f"\nErreurs détaillées:")
        for file_path, error in errors:
            print(f"  - {file_path}: {error}")
EOF
}

# Variables par défaut
VERBOSE=false
DRY_RUN=false

# Analyse des arguments
while [[ $# -gt 0 ]]; do
    case $1 in
        -h|--help)
            show_help
            exit 0
            ;;
        -v|--verbose)
            VERBOSE=true
            shift
            ;;
        -d|--dry-run)
            DRY_RUN=true
            shift
            ;;
        -*)
            echo -e "${RED}Option inconnue: $1${NC}"
            show_help
            exit 1
            ;;
        *)
            SQL_DIR="$1"
            shift
            ;;
    esac
done

# Vérifications
if [[ ! -d "$SQL_DIR" ]]; then
    echo -e "${RED}Erreur: Le répertoire $SQL_DIR n'existe pas${NC}"
    exit 1
fi

check_python

# Affichage des informations
echo -e "${BLUE}Conversion des fichiers SQL en minuscules...${NC}"
echo -e "${BLUE}Répertoire: $SQL_DIR${NC}"
if [[ "$DRY_RUN" == "true" ]]; then
    echo -e "${YELLOW}Mode: Simulation (dry-run)${NC}"
fi
if [[ "$VERBOSE" == "true" ]]; then
    echo -e "${YELLOW}Mode: Verbeux${NC}"
fi
echo -e "${BLUE}$(printf '=%.0s' {1..50})${NC}"

# Création du script Python
create_python_script

# Construction des arguments pour le script Python
PYTHON_ARGS="/tmp/convert_sql_to_lowercase.py \"$SQL_DIR\""
if [[ "$DRY_RUN" == "true" ]]; then
    PYTHON_ARGS="$PYTHON_ARGS --dry-run"
fi
if [[ "$VERBOSE" == "true" ]]; then
    PYTHON_ARGS="$PYTHON_ARGS --verbose"
fi

# Exécution du script Python
eval "python3 $PYTHON_ARGS"

# Nettoyage
rm -f /tmp/convert_sql_to_lowercase.py

echo -e "${GREEN}Terminé!${NC}"
