<?php
// Arquivo: C:/xampp/htdocs/campo_minado/api/partidas.php

header("Content-Type: application/json");
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: GET, POST, OPTIONS");
header("Access-Control-Allow-Headers: Content-Type");

// Handle preflight OPTIONS request
if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit();
}

// Database connection
$host = "localhost";
$user = "root";
$pass = "";          // Default XAMPP password is empty
$db   = "campo_minado";

$conn = new mysqli($host, $user, $pass, $db);

if ($conn->connect_error) {
    http_response_code(500);
    echo json_encode(["error" => "Falha na conexão: " . $conn->connect_error]);
    exit();
}

$method = $_SERVER['REQUEST_METHOD'];

// GET - list all matches
if ($method === 'GET') {
    $sql = "SELECT id, nome, pontuacao, data FROM partidas ORDER BY pontuacao DESC";
    $result = $conn->query($sql);

    $partidas = [];
    while ($row = $result->fetch_assoc()) {
        $partidas[] = $row;
    }

    echo json_encode($partidas);
}

// POST - save a new match
elseif ($method === 'POST') {
    $data = json_decode(file_get_contents("php://input"), true);

    if (!isset($data['nome']) || !isset($data['pontuacao']) || !isset($data['data'])) {
        http_response_code(400);
        echo json_encode(["error" => "Dados incompletos"]);
        exit();
    }

    $nome      = $conn->real_escape_string($data['nome']);
    $pontuacao = (int) $data['pontuacao'];
    $dataVal   = $conn->real_escape_string($data['data']);

    $sql = "INSERT INTO partidas (nome, pontuacao, data) VALUES ('$nome', $pontuacao, '$dataVal')";

    if ($conn->query($sql)) {
        http_response_code(201);
        echo json_encode(["message" => "Partida salva com sucesso", "id" => $conn->insert_id]);
    } else {
        http_response_code(500);
        echo json_encode(["error" => "Erro ao salvar: " . $conn->error]);
    }
}

$conn->close();
?>
