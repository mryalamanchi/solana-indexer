@Library('shared-library@DO-233/multi-base-image-pipeline') _

def pipelineConfig = [
    "stackName": "protocol-solana",
    "services": [
        [name: 'solana-migration', path: './solana-migration'],
        [name: 'solana-indexer', path: './solana-indexer'],
        [name: 'solana-api', path: './solana-api']
    ],
    "slackChannel": "#protocol-duty"
    // "baseImageVersion": "11-9e56e2-9"
]

serviceCI(pipelineConfig)
