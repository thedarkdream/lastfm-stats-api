export default {
  data() {
    this.chart = null;
  },
    props: {
        timeline: Object,
        chartId: {
            type: String,
            default: "myChart"
        }
    },
    template: `
        <canvas :id="chartId" class="canvasClass"></canvas>
    `,
    methods: {
        render() {
            const ctx = document.getElementById(this.chartId);
            this.chart = new Chart(ctx, {
                type: 'line',
                data: {
                  labels: this.timeline.labels,
                  datasets: this.timeline.points.map((o) => createDataItem(o))
                },
                options: {
                  scales: {
                    y: {
                      beginAtZero: true
                    }
                  }
                }
            });
        }
    },
    mounted: function() {
        this.render();
    },
    watch: {
        timeline: function(oldVal, newVal) {
            this.chart.data.labels = this.timeline.labels;
            this.chart.data.datasets = this.timeline.points.map((o) => createDataItem(o));
            this.chart.update();
        }
    }
}

var createDataItem = function(o) {
    return {
        label: o.artist,
        data: o.points,
        borderWidth: 3,
        tension: 0.3
    }
};
