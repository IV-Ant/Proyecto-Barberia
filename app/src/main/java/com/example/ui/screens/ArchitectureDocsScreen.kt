package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DataObject
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.BentoCard
import com.example.ui.components.BentoSectionHeader
import com.example.ui.theme.BarberCyan
import com.example.ui.theme.BarberEmerald
import com.example.ui.theme.BarberGoldAmber
import com.example.ui.theme.BarberGoldLight
import com.example.ui.theme.BarberGoldPrimary
import com.example.ui.theme.BarberPurple
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateCard
import com.example.ui.theme.SlateCardElevated
import com.example.ui.theme.SlateDarkBackground
import com.example.ui.theme.SlateSurface
import com.example.ui.theme.SlateSurfaceVariant
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun ArchitectureDocsScreen(modifier: Modifier = Modifier) {
    var subTab by remember { mutableIntStateOf(0) } // 0: Diagrama de Arquitectura, 1: Modelo SQL 3FN, 2: Flujo de Citas
    val context = LocalContext.current

    val sqlSchemaText = """
-- ==========================================================
-- MODELO DE BASE DE DATOS RELACIONAL (POSTGRESQL - 3FN)
-- SISTEMA DE GESTIÓN DE BARBERÍAS Y CITAS (COP)
-- ==========================================================

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- 1. TABLA: USUARIOS (Clientes, Barberos y Dueños)
CREATE TABLE usuarios (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    nombre VARCHAR(120) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    telefono VARCHAR(20) NOT NULL,
    rol VARCHAR(20) NOT NULL CHECK (rol IN ('cliente', 'barbero', 'dueño')),
    foto_url TEXT,
    modalidad VARCHAR(20) CHECK (modalidad IN ('independiente', 'empleado') OR modalidad IS NULL),
    fecha_creacion TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    activo BOOLEAN DEFAULT TRUE,
    CONSTRAINT chk_modalidad_barbero CHECK (
        (rol = 'barbero' AND modalidad IS NOT NULL) OR 
        (rol != 'barbero' AND modalidad IS NULL)
    )
);

-- 2. TABLA: LOCAL (Barberías)
CREATE TABLE locales (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    id_dueno UUID NOT NULL REFERENCES usuarios(id) ON DELETE RESTRICT,
    nombre VARCHAR(150) NOT NULL,
    direccion VARCHAR(255) NOT NULL,
    ciudad VARCHAR(100) NOT NULL DEFAULT 'Bogotá',
    telefono VARCHAR(20) NOT NULL,
    horarios VARCHAR(100) NOT NULL,
    fecha_registro TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 3. TABLA: SERVICIOS (Catálogo general o del barbero)
CREATE TABLE servicios (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    id_local UUID NOT NULL REFERENCES locales(id) ON DELETE CASCADE,
    id_barbero UUID REFERENCES usuarios(id) ON DELETE SET NULL, -- NULL si es del local
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    costo_cop NUMERIC(12, 2) NOT NULL CHECK (costo_cop > 0),
    duracion_minutos INTEGER NOT NULL DEFAULT 30,
    activo BOOLEAN DEFAULT TRUE
);

-- 4. TABLA: DISPONIBILIDAD (Horarios por barbero)
CREATE TABLE disponibilidad (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    id_barbero UUID NOT NULL REFERENCES usuarios(id) ON DELETE CASCADE,
    dia_semana INTEGER NOT NULL CHECK (dia_semana BETWEEN 0 AND 6), -- 0=Dom, 1=Lun
    hora_inicio TIME NOT NULL,
    hora_fin TIME NOT NULL,
    disponible BOOLEAN DEFAULT TRUE,
    UNIQUE(id_barbero, dia_semana)
);

-- 5. TABLA: CITAS (Agendamiento con Estados de Flujo)
CREATE TABLE citas (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    id_cliente UUID NOT NULL REFERENCES usuarios(id) ON DELETE RESTRICT,
    id_barbero UUID NOT NULL REFERENCES usuarios(id) ON DELETE RESTRICT,
    id_local UUID NOT NULL REFERENCES locales(id) ON DELETE RESTRICT,
    id_servicio UUID NOT NULL REFERENCES servicios(id) ON DELETE RESTRICT,
    fecha DATE NOT NULL,
    hora TIME NOT NULL,
    estado VARCHAR(25) NOT NULL DEFAULT 'PENDIENTE' 
        CHECK (estado IN ('PENDIENTE', 'CONFIRMADA', 'RECHAZADA', 'CANCELADA', 'COMPLETADA')),
    motivo_rechazo_cancelacion TEXT,
    metodo_pago VARCHAR(25) NOT NULL CHECK (metodo_pago IN ('EFECTIVO', 'TRANSFERENCIA')),
    valor_cop NUMERIC(12, 2) NOT NULL CHECK (valor_cop > 0),
    comprobante_url TEXT,
    notificacion_enviada BOOLEAN DEFAULT FALSE,
    fecha_creacion TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 6. TABLA: RESEÑAS (Solo verificadas tras completar cita)
CREATE TABLE resenas (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    id_cita UUID UNIQUE NOT NULL REFERENCES citas(id) ON DELETE CASCADE,
    id_cliente UUID NOT NULL REFERENCES usuarios(id) ON DELETE RESTRICT,
    id_barbero UUID NOT NULL REFERENCES usuarios(id) ON DELETE RESTRICT,
    valoracion INTEGER NOT NULL CHECK (valoracion BETWEEN 1 AND 5),
    comentario TEXT NOT NULL,
    fecha TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- ÍNDICES ESTRATÉGICOS PARA CONSULTAS DE ALTA VELOCIDAD
CREATE INDEX idx_citas_barbero_fecha ON citas(id_barbero, fecha, hora);
CREATE INDEX idx_citas_cliente ON citas(id_cliente);
CREATE INDEX idx_citas_estado ON citas(estado);
CREATE INDEX idx_servicios_local ON servicios(id_local);
CREATE INDEX idx_resenas_barbero ON resenas(id_barbero);
    """.trimIndent()

    Column(modifier = modifier.fillMaxSize()) {
        ScrollableTabRow(
            selectedTabIndex = subTab,
            containerColor = SlateDarkBackground,
            contentColor = BarberGoldPrimary,
            edgePadding = 16.dp,
            divider = {}
        ) {
            Tab(
                selected = subTab == 0,
                onClick = { subTab = 0 },
                text = {
                    Text(
                        "📐 Arquitectura",
                        fontWeight = if (subTab == 0) FontWeight.Bold else FontWeight.Normal,
                        color = if (subTab == 0) BarberGoldAmber else TextSecondary
                    )
                }
            )
            Tab(
                selected = subTab == 1,
                onClick = { subTab = 1 },
                text = {
                    Text(
                        "🗄️ Modelo SQL (3FN)",
                        fontWeight = if (subTab == 1) FontWeight.Bold else FontWeight.Normal,
                        color = if (subTab == 1) BarberGoldAmber else TextSecondary
                    )
                }
            )
            Tab(
                selected = subTab == 2,
                onClick = { subTab = 2 },
                text = {
                    Text(
                        "🔄 Flujo de Citas",
                        fontWeight = if (subTab == 2) FontWeight.Bold else FontWeight.Normal,
                        color = if (subTab == 2) BarberGoldAmber else TextSecondary
                    )
                }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        when (subTab) {
            0 -> ArchitectureDiagramView()
            1 -> SqlModelView(sqlSchemaText) {
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("BarberHub SQL Schema", sqlSchemaText)
                clipboard.setPrimaryClip(clip)
                Toast.makeText(context, "SQL 3FN copiado al portapapeles", Toast.LENGTH_SHORT).show()
            }
            2 -> BookingFlowView()
        }
    }
}

@Composable
fun ArchitectureDiagramView() {
    val modules = listOf(
        Triple("Frontend Móvil & Web", "Android Jetpack Compose + React Web Dashboard para dueños con diseño Bento Grid, navegación modular, offline-first Room y soporte de roles.", Icons.Default.Widgets),
        Triple("Base de Datos (3FN)", "PostgreSQL / SQLite Room normalizado en 3FN con tablas usuarios, locales, servicios, disponibilidad, citas y reseñas con llaves foráneas e integridad referencial.", Icons.Default.Storage),
        Triple("Notificaciones Push", "Firebase Cloud Messaging (FCM) para disparar avisos en tiempo real: nueva cita solicitada, confirmada, rechazada, recordatorio previo y reseña disponible.", Icons.Default.Notifications),
        Triple("Seguridad & Roles", "Control de Acceso Basado en Roles (RBAC): Cliente (solo citas e historial), Barbero (agenda y validación de pagos), Dueño (local, métricas y catálogo).", Icons.Default.Security),
        Triple("Gestión de Pagos COP", "Flujo transparente de pagos en pesos colombianos con soporte de Efectivo en local o Transferencia digital con comprobante almacenado en Firebase Storage.", Icons.Default.AccountTree),
        Triple("Reseñas Verificadas", "Garantía de calidad: Solo los clientes con citas en estado COMPLETADA pueden publicar una valoración con calificación de 1 a 5 estrellas.", Icons.Default.DataObject)
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            BentoCard(
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = 16.dp,
                containerColor = SlateCard,
                borderColor = SlateBorder
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "ARQUITECTURA DE LA PLATAFORMA",
                        color = TextMuted,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "BarberHub: Sistema Integral de Gestión de Barberías",
                        color = BarberGoldAmber,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Plataforma multi-rol para Colombia con moneda COP, gestión de citas, modalidades de barberos y reportería.",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        items(modules) { (title, desc, icon) ->
            BentoCard(
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = 14.dp,
                containerColor = SlateCard,
                borderColor = SlateBorder
            ) {
                Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.Top) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0x33F59E0B)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = icon, contentDescription = null, tint = BarberGoldPrimary, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = title, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = desc, color = TextSecondary, fontSize = 12.sp, lineHeight = 16.sp)
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun SqlModelView(sqlText: String, onCopy: () -> Unit) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Script DDL PostgreSQL (3FN)",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Button(
                    onClick = onCopy,
                    colors = ButtonDefaults.buttonColors(containerColor = BarberGoldPrimary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, tint = TextDark, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Copiar SQL", color = TextDark, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }

        item {
            BentoCard(
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = 14.dp,
                containerColor = Color(0xFF0A0F1D),
                borderColor = SlateBorder
            ) {
                Text(
                    text = sqlText,
                    color = Color(0xFFE2E8F0),
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    lineHeight = 15.sp,
                    modifier = Modifier.padding(14.dp)
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun BookingFlowView() {
    val steps = listOf(
        "1. Selección & Solicitud" to "Cliente explora barberos, escoge fecha, horario y servicio. Selecciona método de pago (Efectivo o Transferencia con comprobante). Cita queda en estado 'PENDIENTE'.",
        "2. Notificación al Barbero" to "FCM envía notificación push instantánea al barbero asignado con el resumen de la solicitud.",
        "3. Decisión del Barbero" to "El barbero revisa su disponibilidad y decide: 'CONFIRMAR' o 'RECHAZAR' (con motivo). Si se confirma, el horario queda bloqueado en la agenda.",
        "4. Notificación al Cliente" to "Cliente recibe confirmación con detalles de fecha, hora, barbero, local y recordatorio de cancelación con mínimo 1h de anticipación.",
        "5. Atención & Verificación de Pago" to "El barbero atiende al cliente. Verifica el pago en efectivo o revisa el comprobante adjunto en Firebase Storage y presiona 'VERIFICAR PAGO & COMPLETAR'.",
        "6. Reseña Verificada" to "Solo una vez la cita está 'COMPLETADA', el sistema habilita el formulario de reseña verificada (1 a 5 estrellas + comentario) en el perfil del barbero."
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(steps) { (stepTitle, stepDesc) ->
            BentoCard(
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = 14.dp,
                containerColor = SlateCard,
                borderColor = SlateBorder
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(text = stepTitle, color = BarberGoldAmber, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = stepDesc, color = TextSecondary, fontSize = 12.sp, lineHeight = 16.sp)
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
